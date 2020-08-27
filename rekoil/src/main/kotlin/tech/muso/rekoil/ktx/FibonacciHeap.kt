package tech.muso.rekoil.ktx

import java.util.function.Consumer

class FibonacciHeap2<T : Comparable<T>>(type: HeapProperty = HeapProperty.MINIMUM) {

    enum class HeapProperty {
        MINIMUM,
        MAXIMUM
    }

    val compare: (Int) -> Boolean =
            when(type) {
                HeapProperty.MINIMUM -> { it -> it < 0 }
                HeapProperty.MAXIMUM -> { it -> it > 0 }
            }

    inline fun compare(node: Node<T>, rootNode: Node<T>): Boolean {
        return compare(node.value.compareTo(rootNode.value))
    }

    data class Node<T : Comparable<T>>(
            var value: T,
            var degree: Int = 0,    // number of children in list
            var parent: Node<T>? = null,
            var child: Node<T>? = null,
            // lost child marker
            var mark: Boolean = false
    ) : Iterable<Node<T>> {

        // doubly-linked list of siblings (child nodes of same degree)
        var left: Node<T> = this@Node
        var right: Node<T> = this@Node

        // TODO: iterator over children.
        override fun forEach(action: Consumer<in Node<T>>?) {

        }

        override fun iterator(): Iterator<Node<T>> {
            TODO("Not yet implemented")
        }


        /**
         * Iterate over the doubly linked list of a given node.
         * Yielding the head node last.
         */
        fun iterate() = sequence {
            var node: Node<T>? = this@Node
            val stop = this@Node
            var flag = false
            while (true) {
                if (node == stop && flag) {
                    break
                } else if (node == stop) {
                    flag = true
                }
                yield(node)
                node = node!!.right
            }
        }

        /**
         * Remove a node from the doubly linked child list.
         */
        internal inline fun removeChild(removedChild: Node<T>) {
            // if the child has no siblings (right/left) equal itself
            if (this.child == this.child?.right) {
                // then the child we are removing is assumed to be our child. TODO: validate!! saves call but incorrect assumption.
                this.child = null   // then remove child directly
                // TODO: can probably return here.
            } else if (this.child == removedChild) {
                // if our child has siblings, assign by join right
                this.child = removedChild.right
                removedChild.right.parent = this
            }
            // re-link the side nodes of the removed child
            removedChild.left.right = removedChild.right
            removedChild.right.left = removedChild.left
        }
    }

    // NOTE: technically these are the same,
    // as the minNode lives in the root list,
    // and it's a self containing doubly-linked list
    private var rootList: Node<T>? = null
    private var minNode: Node<T>?  = null

    var totalNodes: Int = 0     // maintain count of nodes in heap



    /**
     * Insert into the unordered root list with O(1) time.
     * Immediately caching a new minimum.
     */
    fun insert(data: T) {
        // initialize node
        val node = Node<T>(data)
        node.left = node
        node.right = node
        add_node_to_root_list(node)

        // if we don't have a min, or the new node is less than the current min
        if (minNode == null || compare(node, minNode!!)) { // TODO: write kotlinic
            // set the node as the root
            minNode = node
        }
        totalNodes++
    }

    fun add_node_to_root_list(node: Node<T>) {
        // NOTE: null check is one line of bytecode faster than lateinit check
        //  just as !! for known non-null is one less than lateinit access
        if (rootList == null) {
            rootList = node
        } else {
            node.right = rootList!!.right
            node.left = rootList!!
            rootList!!.right!!.left = node
            rootList!!.right = node
        }
    }

    fun merge_with_child_list(parent: Node<T>, node: Node<T>) {
        if (parent.child == null) {
            parent.child = node
        } else {
            node.right = parent.child!!.right
            node.left = parent.child!!
            // TODO: refactor the insertion of the node in the child list.
            parent.child!!.right.left = node
            parent.child!!.right = node
        }
    }

    fun decrease_key(node: Node<T>, data: T) {
        // dont attempt to increase key // TODO: special case for if root.
        if (data > node.value) return
        node.value = data
        val parent = node.parent
        // if there is a parent, compare the value
        if (parent != null && compare(node, parent)) {
            // and preserve the heap property via cut operations
            cut(node, parent)
            cascading_cut(parent)
        }
        // update min node if the new value is less than the current min
        if (compare(node, minNode!!)) {
            minNode = node
        }
    }

    /**
     * If a child node becomes smaller than its parent node,
     * abandon child, and move it to the root list.
     */
    fun cut(child: Node<T>, parent: Node<T>) {
        parent.removeChild(child)
        parent.degree--
        add_node_to_root_list(child)
        child.parent = null
        child.mark = false // reset mark whenever a child has been abandoned.
    }

    /**
     * To improve time bounds, perform on a parent
     */
    tailrec fun cascading_cut(node: Node<T>) {
        val parent = node.parent
        if (parent != null) {
            // if parent is not marked, mark it (has lost a single child)
            if (!parent.mark) {
                parent.mark = true
            } else { // if it has lost more than one child (already marked)
                cut(node, parent)
                cascading_cut(parent)
            }
        }
    }


    /**
     * Combine root nodes of equal degree to consolidate the heap
     * by creating a list of unordered binomial trees
     */
    fun consolidate() {

        // allocate space for all the nodes in our heap
        val allNodes = arrayOfNulls<Node<T>?>(size=totalNodes)
        // iterate over the nodes in the root list
        rootList?.iterate()?.iterator()?.forEach {
            // initialize degree for layer of graph we are in
            var degree = it!!.degree
            // compare the current node in the root list
            var x: Node<T> = it
            // to the other non-null nodes higher in the list
            while (allNodes[degree] != null) {
                var y = allNodes[degree]
                // if the heap condition is violated, swap the two nodes in place
                if (compare(y!!, it)) {
                    val temp = it
                    x = y
                    y = temp
                }
                // then link the two nodes
                heap_link(y, x)
                allNodes[degree] = null
                degree++
            }
            // reassign node in list
            allNodes[degree] = it
        }
        // now that we have list of heaps, find a new min node
        // no need to remake new root list (created in heap_link())
        allNodes.forEach {
            it?.let {
                // compare to current min and update if we are lower
                if(compare(it, minNode!!)) {
                    minNode = it
                }
            }
        }
    }

    fun remove_from_root_list(node: Node<T>) {
        if (node == rootList) {
            rootList = node.right
        }

        node.left.right = node.right
        node.right.left = node.left
    }

    /**
     * Link one node to another in the root list,
     * and update its child list to reflect the move.
     */
    fun heap_link(y: Node<T>, x: Node<T>) {
        // remove from root list
        remove_from_root_list(y)
        // reset link to self
        y.left = y
        y.right = y
        // merge the child lists
        merge_with_child_list(x, y)
        x.degree++      // increment degree to reflect
        y.parent = x    // link the parent
        y.mark = false  // reset mark for having lost children
    }

}


