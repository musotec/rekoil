package tech.muso.rekoil.core

import kotlinx.coroutines.*
import tech.muso.rekoil.RekoilLazyNodeRegistrationException
import kotlin.coroutines.CoroutineContext

// TODO: Transform function for T -> R as an alias for single node selector transformation
//public interface TransformSelector<T, R>: Selector<R>

internal class SelectorImpl<T> (
        coroutineScope: CoroutineScope,
        parent: RekoilContext,
        private val supervisorContext: SupervisorRekoilContextImpl,
        private var selectorScope: suspend SelectorScope.() -> T
) : Selector<T?>, SelectorRekoilScopeImpl<T?>(parent, supervisorContext.rootNode, coroutineScope),
        CoroutineContext by coroutineScope.coroutineContext {   // TODO: fix cancellation behavior by creating own scope

    // TODO: empty dependency list passed for when/if we can evaluate the
    //  dependencies another way. without waiting for the result of the value
    internal class SelectorDependencyEvaluatorScopeImpl(
            val selectorContext: SelectorImpl<*>,
            val parentContext: SupervisorRekoilContextImpl,
            emptyDependencyList: MutableList<RekoilContext.Key<*>>
    ) : SelectorScope, RekoilScope by selectorContext {

        override suspend fun <R> get(node: RekoilContext.ValueNode<R>): R {
            // TODO: THROW AN EXCEPTION IF THERE IS A PROBLEM WITH THE GRAPH LOADING ACTUAL DATA
            //   OR HANGING TOO LONG. IN WHICH CASE A DEFAULT NEEDS TO BE SET FOR THAT ATOM.
            return parentContext.getAndRegister(selectorContext.key, node)
                .also { Log.s("$selectorContext ~~> get($node): $it") }
        }

        override fun release() {
//            selectorContext.release()
            this.releaseScope() // TODO: verify this functionality.
        }
    }

    // member implementations
    override val key by lazy { object : RekoilContext.Key<SelectorImpl<T>>{} }

    private var selectorEvaluatorJob: Job? = null

    override fun invalidate() {
        Log.v("$this.invalidate() [valid:$valid]")

        // only allow one invalidation at a time
        if (valid == false) {
            selectorEvaluatorJob?.cancel()
        }

        valid = false // reset the flag
        Log.s("${this@SelectorImpl}.valid = false")

        // TODO: should we forcefully invalidate all the children in our scope??
        //  This would require isValid bypass; release of pending jobs; and Atom.invalidate().

        // FIXME: ongoing job should be cancelled if release() is called.

        // run on an async scope so that we can suspend during subsequent get(node) calls
        selectorEvaluatorJob = coroutineScope.async {
            // evaluate the block within the selector
            val result = selectorScope.invoke(
                    SelectorDependencyEvaluatorScopeImpl(
                            selectorContext = this@SelectorImpl,
                            parentContext = supervisorContext,
                            emptyDependencyList = dependencies
                    )
            )
            value = result
        }.also {
            // set valid flag on completion without error
            it.invokeOnCompletion { exception ->
                // when no exception, set state to valid
                if (exception == null) {
                    Log.s("${this@SelectorImpl}.valid = true")
                    valid = true
                    return@invokeOnCompletion
                }

                Log.e("Error: ${exception.localizedMessage} [$exception]")

                // for cancellation, do not elevate error.
                if (exception is CancellationException) {
                    return@invokeOnCompletion
                }

                // FIXME: Do not uncomment. There is likely a problem with the setup of your selector.
//                if (exception is NoSuchElementException) {
//                    return@invokeOnCompletion
//                }

                // if any error other than LazyRegistration (will re-evaluate?)
                // then elevate the exception
                if (exception !is RekoilLazyNodeRegistrationException) {
                    throw exception
                }

                // if not cancellation, then we need to elevate error and crash.
//                if (exception !is CancellationException) {
//                    when(exception) {
////                        is NullPointerException -> throw RekoilDependencyException("RekoilValue not found within the current scope!")
//                        else -> throw exception
//                    }
//                }
            }
        }
    }

    // what nodes does this selector depend on?
    // TODO: remove this or use this before first major version release.
    private val dependencies = mutableListOf<RekoilContext.Key<*>>()

    @Volatile internal var valid: Boolean = false

    // NOTE: This value is the cached value.
    @ExperimentalCoroutinesApi
    @Volatile override var value: T? = null // marked volatile for multi-thread value obtaining.
    set(value) { // NOTE: ORDER OF OPERATIONS IS IMPORTANT
        // update the backing field first so that observers getting this value immediately receive
        field = value
        send(value) {
            // when we update our value, notify the parent.
            Log.s("$shortName send($it)")
        }
    }

    // FIXME: finish this implementation.
    override fun <R : T?> changeSelector(selectorScope: suspend SelectorScope.() -> R) {
        TODO("Finish changeSelector() implementation.")

        // clear dependencies
        dependencies.clear()

        // clear dependencies in the scope of this selector
//        _rekoil.resetDependencies(this)

//        this.selectorScope = selectorScope
        // TODO: this should create a new Selector object so that the return value can match.
    }

    init {
        // NOTE: ORDER OF OPERATIONS IS IMPORTANT
        supervisorContext.register(this)   // register before we invalidate to get our result
        invalidate()                            // invalidate to generate and push to parent
    }

    private inline val shortName: String get() = "Selector<${(value as? Any?)?.javaClass?.name?.substringAfterLast('.')}>[${this.identifier}]"

    override fun toString(): String {
        return "$shortName($value)"
    }

    override fun hashCode(): Int {
        return super.hashCode() // TODO: May break unique identifier when overridden.
    }
}