/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with distinct integer keys and info
 *
 */

public class WAVLTree_rinatsikunda_shaharlerner {
	WAVLNode root;
	int size;

	public WAVLTree_rinatsikunda_shaharlerner() {
		this.root = null;
		this.size = 0;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return this.root == null;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		WAVLNode output = searchNodeReturn(k);
		if (output == null || output.key != k) {
			return null;
		} else {
			return output.info;
		}
	}
	
	//return the node that contains key "k" or its potential parent
	private WAVLNode searchNodeReturn(int k) {
		if (this.empty()) {
			return null;
		}
		WAVLNode currentNode = this.root;
		while (currentNode != null) {
			if (k == currentNode.key) {
				return currentNode;
			}
			if (k > currentNode.key) {
				if (currentNode.right != null) {
					currentNode = currentNode.right;
				} else {
					return currentNode;
				}
			} else {
				if (currentNode.left != null) {
					currentNode = currentNode.left;
				} else {
					return currentNode;
				}
			}
		}
		return currentNode;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must
	 * remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were necessary. returns -1
	 * if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		WAVLNode newNode = new WAVLNode(k, i);
		newNode.rank = 0;
		int whichCase;
		int[] complexityCounter = { 0 }; // using array in order to increment
											// inside assisting functions
		WAVLNode searchStopped = searchNodeReturn(k);
		if (searchStopped != null){
			if (searchStopped.key == k) { // key already exists
				return -1;
			}
		}
		this.size++;
		if (searchStopped == null) {
			this.root = newNode;	
		} else {
			if (k > searchStopped.key) {
				searchStopped.right = newNode;
				searchStopped.right.parent = searchStopped;
			} else {
				searchStopped.left = newNode;
				searchStopped.left.parent = searchStopped;
			}
			WAVLNode currentNode=searchStopped;
			whichCase = checkCase(currentNode);
			if (whichCase == 11 || whichCase == 12) { // case A
				currentNode = promote(currentNode, complexityCounter);
				whichCase = checkCase(currentNode);
			}
			WAVLNode rotatedNode = null;
			switch (whichCase) {
			/* 21 - case 2 left (1,2) <-(0,2) 
			 * 22 - case 2 right (2,0)-> (2,1) 
			 * 31 - case 3 left (2,1) <-(0,2) 
			 * 32 - case 3 right (2,0)->(1,2)*/
			case 21:
				rotatedNode = currentNode.left;
				rotateRight(rotatedNode);
				rotatedNode.right.rank--;
				complexityCounter[0]=complexityCounter[0]+1;
				break;
			case 22:
				rotatedNode = currentNode.right;
				rotateLeft(rotatedNode);
				rotatedNode.left.rank--;
				complexityCounter[0]=complexityCounter[0]+1;
				break;
			case 31:
				rotatedNode = currentNode.left.right;
				rotateLeft(rotatedNode);
				rotateRight(rotatedNode);
				rotatedNode.rank++;
				rotatedNode.left.rank--;
				rotatedNode.right.rank--;
				complexityCounter[0]=complexityCounter[0]+2;
				break;
			case 32:
				rotatedNode = currentNode.right.left;
				rotateRight(rotatedNode);
				rotateLeft(rotatedNode);
				rotatedNode.rank++;
				rotatedNode.left.rank--;
				rotatedNode.right.rank--;
				complexityCounter[0]=complexityCounter[0]+2;
				break;
			}
		}
		return complexityCounter[0];
	}
	
	/*
	 * returns cases for problematic situations: 11 - case 1 left (0,1) 12 -
	 * case 1 right (1,0) 21 - case 2 left (1,2) <-(0,2) 22 - case 2 right
	 * (2,0)-> (2,1) 31 - case 3 left (2,1) <-(0,2) 32 - case 3 right (2,0)->
	 * (1,2)
	 * 
	 * 4 - no problem (2,1 or 1,2 or 1,1 or 2,2)
	 */
	private int checkCase(WAVLNode node) {
		if (node == null) {
			return -1;
		}
		if (node.rankDifferanceleft() == 0 && node.rankDifferanceRight() == 1) {
			return 11;
		}
		if (node.rankDifferanceleft() == 1 && node.rankDifferanceRight() == 0) {
			return 12;
		}
		if (node.rankDifferanceleft() == 0 && node.rankDifferanceRight() == 2) {
			if (node.left.rankDifferanceleft() == 2
					&& node.left.rankDifferanceRight() == 1) {
				return 31;
			}
			if (node.left.rankDifferanceleft() == 1
					&& node.left.rankDifferanceRight() == 2) {
				return 21;
			}
		}
		if (node.rankDifferanceleft() == 2 && node.rankDifferanceRight() == 0) {
			if (node.right.rankDifferanceleft() == 1
					&& node.right.rankDifferanceRight() == 2) {
				return 32;
			}
			if (node.right.rankDifferanceleft() == 2
					&& node.right.rankDifferanceRight() == 1) {
				return 22;
			}
		}
		return 4;
	}
	// promotes until doesn't have to. returns the node that it didn't promote
		private WAVLNode promote(WAVLNode node, int[] counter) {
			node.rank++;
			counter[0]++;
			if (node != this.root) {
				WAVLNode tempNode = node.parent;
				int whichCase = checkCase(tempNode);
				while (whichCase == 11 || whichCase == 12) { //cases needed for promotions
					tempNode.rank++;
					counter[0]++;
					if (tempNode.isRoot()) {
						break;
					}
					tempNode = tempNode.parent;
					whichCase = checkCase(tempNode);
				}
				return tempNode;
			} else {
				return node;
			}
		}
	private void rotateRight(WAVLNode rotatedNode) {
		if (rotatedNode.right != null) {
			rotatedNode.right.parent = rotatedNode.parent;
		}
		rotatedNode.parent.left = rotatedNode.right;
		rotatedNode.right = rotatedNode.parent;
		changeParents(rotatedNode);
	}

	private void rotateLeft(WAVLNode rotatedNode) {
		if (rotatedNode.left != null) {
			rotatedNode.left.parent = rotatedNode.parent;
		}
		rotatedNode.parent.right = rotatedNode.left;
		rotatedNode.left = rotatedNode.parent;
		changeParents(rotatedNode);
	}

	private void changeParents(WAVLNode rotatedNode) {
		WAVLNode grandfather = rotatedNode.parent.parent;
		WAVLNode previousFather = rotatedNode.parent;
		if (grandfather != null) { // check that rotateNode's parent is not the root
			if (grandfather.right == rotatedNode.parent) {
				grandfather.right = rotatedNode; // make rotatedNode the son of
													// his previous Grandfather
				rotatedNode.parent = grandfather; // update rotatedNode's
													// "parent" to his previous
													// grandfather
				previousFather.parent = rotatedNode; // update previous father's
														// "parent" to
														// rotatedNode
			} else {
				grandfather.left = rotatedNode;
				rotatedNode.parent = grandfather;
				previousFather.parent = rotatedNode;
			}
		} else {
			this.root.parent = rotatedNode;
			this.root = rotatedNode;
			rotatedNode.parent = null;
		}
	}


	// 0 - (2,2) - if is a leaf with rank of 1, must demote!
	// 11 - (3,2) 
	// 12 - (2,3) 
	// 21 - (3,1) -> (2,2)
	// 22 - (2,2) <- (1,3)
	// 31 - (3,1) -> (1,1)
	// 32 - (1,1) <- (1,3)
	// 33 - (3,1) -> (2,1)
	// 34 - (1,2) <- (1,3)
	// 41 - (3,1) -> (1,2)
	// 42 - (2,1) <- (1,3)
	// -1 - OK

	private int checkCaseDelete(WAVLNode node) {
		if (node == null) {
			return -1;
		}
		if (node.rankDifferanceleft() == 2 && node.rankDifferanceRight() == 2 && node.rank == 1){
			return 0;
		}
		if (node.rankDifferanceleft() == 3 && node.rankDifferanceRight() == 2){
			return 11; //
		}
		if (node.rankDifferanceleft() == 2 && node.rankDifferanceRight() == 3){
			return 12;
		}
		if (node.rankDifferanceleft() == 3 && node.rankDifferanceRight() == 1){
			if (node.right.rankDifferanceleft() == 2 && node.right.rankDifferanceRight() == 2){
				return 21;
			}
			if (node.right.rankDifferanceleft() == 1 && node.right.rankDifferanceRight() == 1){
				return 31;
			}
			if (node.right.rankDifferanceleft() == 2 && node.right.rankDifferanceRight() == 1){
				return 33;
			}
			if (node.right.rankDifferanceleft() == 1 && node.right.rankDifferanceRight() == 2){
				return 41;
			}
		}
		if (node.rankDifferanceleft() == 1 && node.rankDifferanceRight() == 3){
			if (node.left.rankDifferanceleft() == 2 && node.left.rankDifferanceRight() == 2){
				return 22;
			}
			if (node.left.rankDifferanceleft() == 1 && node.left.rankDifferanceRight() == 1){
				return 32;
			}
			if (node.left.rankDifferanceleft() == 1 && node.left.rankDifferanceRight() == 2){
				return 34;
			}
			if (node.left.rankDifferanceleft() == 2 && node.left.rankDifferanceRight() == 1){
				return 42;
			}
		}
		//if OK:
		return -1;
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of
	 * rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		int[] complexityCounter = {0};
		WAVLNode toRemove = searchNodeReturn(k);
		if (toRemove == null || toRemove.key!=k) {
			return -1;
		}
		this.size -- ;
		//tree consists only of a root:
		if (toRemove.isRoot() && toRemove.isLeaf()){
			this.root = null;
			return 0;
		}
		WAVLNode currentNode = toRemove.parent;
		currentNode = startDemotionsAt(toRemove); /*prepares the tree for demotions 
													and return the relevant node 
													for the beginning of demoting*/
		int whichCase = checkCaseDelete(currentNode);
		if (whichCase == 0|| whichCase == 11 || whichCase == 12 ||whichCase == 21 ||whichCase == 22 ) {
			currentNode = demote(currentNode, whichCase, complexityCounter);
		}
		whichCase = checkCaseDelete(currentNode);
		//no more demotions are needed, now must check for for rotations on currentNode:
		WAVLNode rotatedNode = null;
		switch (whichCase) {
		case 31: // 31 - (3,1) -> (1,1)
			rotatedNode = currentNode.right;
			rotateLeft(rotatedNode);
			rotatedNode.left.rank--;
			rotatedNode.rank++;
			complexityCounter[0] += 1 ;
			break;
		case 32: // 32 - (1,1) <- (1,3)
			rotatedNode = currentNode.left;
			rotateRight(rotatedNode);
			rotatedNode.right.rank--;
			rotatedNode.rank++;
			complexityCounter[0] += 1;
			break;
		case 33: // 33 - (3,1) -> (2,1)
			rotatedNode = currentNode.right;
			rotateLeft(rotatedNode);
			rotatedNode.left.rank=rotatedNode.left.rank-2;
			rotatedNode.rank++;
			complexityCounter[0] += 1;
			break;
		case 34: // 34 - (1,2) <- (1,3)
			rotatedNode = currentNode.left;
			rotateRight(rotatedNode);
			rotatedNode.right.rank=rotatedNode.right.rank-2;
			rotatedNode.rank++;
			complexityCounter[0] += 1;
			break;
		case 41: // 41 - (3,1) -> (1,2)
			rotatedNode = currentNode.right.left;
			rotateRight(rotatedNode);
			rotateLeft(rotatedNode);
			rotatedNode.rank=rotatedNode.rank+2;
			rotatedNode.left.rank=rotatedNode.left.rank-2;
			rotatedNode.right.rank--;
			complexityCounter[0] += 2;
			break;
		case 42: // 42 - (2,1) <- (1,3)
			rotatedNode = currentNode.left.right;
			rotateLeft(rotatedNode);
			rotateRight(rotatedNode);
			rotatedNode.rank=rotatedNode.rank+2;
			rotatedNode.right.rank=rotatedNode.right.rank-2;
			rotatedNode.left.rank--;
			complexityCounter[0] += 2;
			break;
		}
		return complexityCounter[0];
	}

	// gets node to remove, deletes and replaces with predecessor if necessary
	// returns the node to start demoting at
	private WAVLNode startDemotionsAt(WAVLNode toRemove) {
		WAVLNode currentNode = null;
		WAVLNode predecessor =null;
		//switch nodes if necessary: 
		if (toRemove.left!=null) {
			if (toRemove.left.right==null){ //the case where toRemove is predecessor's parent
				predecessor=toRemove.left;
				replaceWith(predecessor,toRemove);
			} else {
				predecessor = toRemove.getPredecessor();
				replaceWith(predecessor,toRemove);// replaces with
																// predecessor and
																// gives predecessor
																// rank of toRemove
			}
			if (predecessor.parent==null){
				this.root=predecessor;
			}
		} else if (toRemove.isRoot()) { //if the tree has only a root and a right child
			toRemove.right.parent=null;
			this.root=toRemove.right;
			this.root.rank=0;
			return this.root;
		}
		
		//after switching - remove the node
		
		if (toRemove.isLeaf()) {
			currentNode = toRemove.parent;
			removeFromParent(toRemove);
		} else if (toRemove.isUnaryLeft()) {
			currentNode = toRemove.parent;
			if (toRemove.isLeftChild()){
				toRemove.parent.left=toRemove.left;
				toRemove.left.parent=toRemove.parent;
			}
			else{
				toRemove.parent.right=toRemove.left;
				toRemove.left.parent=toRemove.parent;
			}
		} else if (toRemove.isUnaryRight()) {
			currentNode = toRemove.parent;
			if (toRemove.isLeftChild()){
				toRemove.parent.left=toRemove.right;
				toRemove.right.parent=toRemove.parent;
			}
			else{
				toRemove.parent.right=toRemove.right;
				toRemove.right.parent=toRemove.parent;
			}
		}
		return currentNode;
	}
	// gets leaf and removes it from parent
	private void removeFromParent(WAVLNode toRemove) {
		if (toRemove.isLeftChild()) {
			toRemove.parent.left = null;
			toRemove.parent = null;
		} else {
			toRemove.parent.right = null;
			toRemove.parent = null;
		}
	}
	//demotes or double demotes until OK or until rotations are needed
	private WAVLNode demote(WAVLNode node,int whichCase, int[] complexityCounter) {
		WAVLNode currentNode = node;
		while (whichCase == 0 || whichCase == 11 || whichCase == 12 || whichCase == 21 || whichCase == 22){
			//double demote:
			if (whichCase == 21){
				currentNode.rank --;
				currentNode.right.rank --;
				complexityCounter[0] += 2;//*
			} else if(whichCase == 22) {
				currentNode.rank --;
				currentNode.left.rank --;
				complexityCounter[0] += 2;//*
			//regular demote:
			} else {
				currentNode.rank--;
				complexityCounter[0]+=1;
			}
			if(currentNode.isRoot()){
				break;
			}
			currentNode = currentNode.parent;
			whichCase=checkCaseDelete(currentNode);
		}
		return currentNode;
	}

	/**
	 * public String min()
	 *
	 * Returns the ifo of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		WAVLNode currentNode = this.root;
		if (currentNode == null) {
			return null;
		}
		while (currentNode.left != null) {
			currentNode = currentNode.left;
		}
		return currentNode.info;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if
	 * the tree is empty
	 */
	public String max() {
		WAVLNode currentNode = this.root;
		if (currentNode == null) {
			return null;
		}
		while (currentNode.right != null) {
			currentNode = currentNode.right;
		}
		return currentNode.info;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty
	 * array if the tree is empty.
	 */
	public int[] keysToArray() {
		int[] counter = { 0 }; //using array counter in order 
								//to use it inside the recursion
		int[] arr = new int[this.size];
		if (this.root == null) {
			return arr;
		}
		inOrderAdd(arr, counter, this.root);
		return arr;
	}

	private void inOrderAdd(int[] arr, int[] counter, WAVLNode currentRoot) {
		if (currentRoot != null) {
			inOrderAdd(arr, counter, currentRoot.left);
			arr[counter[0]] = currentRoot.key;
			counter[0]++;
			inOrderAdd(arr, counter, currentRoot.right);
		}
	}

	private void inOrderAdd(String[] arr, int[] counter, WAVLNode currentRoot) {
		if (currentRoot != null) {
			inOrderAdd(arr, counter, currentRoot.left);
			arr[counter[0]] = currentRoot.info;
			counter[0]++;
			inOrderAdd(arr, counter, currentRoot.right);
		}
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		int[] counter = { 0 };
		String[] arr = new String[this.size];
		if (this.root == null) {
			return arr;
		}
		inOrderAdd(arr, counter, this.root);
		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		return this.size;
	}
	
	//replaces nodes with predecessor
	private void replaceWith(WAVLNode predecessor, WAVLNode toRemove) {
		WAVLNode futureParent=toRemove.parent; //changes parents
		if (futureParent!=null){
			if (toRemove.isLeftChild()){
				futureParent.left=predecessor;
			}
			else{
				futureParent.right=predecessor;
			}
		}
		
		/*in case predecessor's parent isn't the 
		  node "toRemove" - remove predecessor from its parent*/ 
		if (predecessor.parent!=toRemove){
			toRemove.left.parent=predecessor;
			if (toRemove.right!=null){
				toRemove.right.parent=predecessor;
			}
			WAVLNode futureRight=toRemove.right;
			WAVLNode futureLeft=toRemove.left;
			toRemove.right=predecessor.right;
			toRemove.left=predecessor.left;
			toRemove.parent=predecessor.parent;
			if (predecessor.left!=null){
				predecessor.left.parent=toRemove;
			}
			predecessor.left = futureLeft;
			predecessor.right = futureRight;
		} else { //when toRemove is predecessor's parent
			WAVLNode futureRight=toRemove.right; /*no need to take care of 
												   left child - it's going to "toRemove"*/
			toRemove.right=null; //there is no right child in this case
			toRemove.left=predecessor.left;
			if (predecessor.left!=null){
				predecessor.left.parent=toRemove;
			}
			toRemove.parent=predecessor;
			predecessor.right=futureRight;
			if (futureRight!=null){
				futureRight.parent=predecessor;
			}
			predecessor.left=toRemove;
		}
		int futureRank=toRemove.rank;
		toRemove.rank=predecessor.rank;
		predecessor.rank=futureRank;
		predecessor.parent = futureParent;
	}
	/**
	 * public class WAVLNode
	 *
	 * If you wish to implement classes other than WAVLTree (for example
	 * WAVLNode), do it in this file, not in another file. This is an example
	 * which can be deleted if no such classes are necessary.
	 */
	public class WAVLNode {
		WAVLNode parent;
		WAVLNode right;
		WAVLNode left;
		int rank;
		int key;
		String info;

		private boolean isUnaryRight() {
			if (this.right != null && this.left == null){
				return true;
			}
			return false;
		}

		private boolean isUnaryLeft() {
			if (this.left != null && this.right == null){
				return true;
			}
			return false;
		}

		public WAVLNode(int key, String info) {
			this.key = key;
			this.info = info;
		}

		private int rankDifferanceRight() {
			int rightRank;
			if (this.right == null) {
				rightRank = -1;
			} else {
				rightRank = this.right.rank;
			}
			return this.rank - rightRank;
		}

		private int rankDifferanceleft() {
			int leftRank;
			if (this.left == null) {
				leftRank = -1;
			} else {
				leftRank = this.left.rank;
			}
			return this.rank - leftRank;
		}

		private boolean isLeaf() {
			if (this.rank == 0) {
				return true;
			} else {
				return false;
			}
		}

		private boolean isRoot() {
			if (this.parent == null) {
				return true;
			} else {
				return false;
			}

		}

		private WAVLNode getPredecessor() {
			WAVLNode predecessor = this;
			// if node doesn't have left child:
			if (predecessor.left == null) {
				if (predecessor.isRightChild()) {
					return predecessor.parent;
				}
				if (predecessor.isLeftChild()) {// go up right all the way:
					while (predecessor.isLeftChild()) {
						predecessor = predecessor.parent;
					}
					// go up left once:
					if (predecessor.isRightChild()) {
						predecessor = predecessor.parent;
					}
					return predecessor;
				}
			
			} else { // if node has left child
				predecessor = predecessor.left;
			}
			// go down right all the way - if possible:
			while (predecessor.right != null) {
				predecessor = predecessor.right;
			}
			return predecessor;
		}


		private boolean isRightChild() {
			if (this.isRoot()) {
				return false;
			}
			if (this.parent.right == this) {
				return true;
			} else {
				return false;
			}
		}

		private boolean isLeftChild() {
			if (this.isRoot()) {
				return false;
			}
			if (this.parent.left == this) {
				return true;
			} else {
				return false;
			}
		}
		public WAVLNode getLeft() {
			return this.left;
		}
		public WAVLNode getRight() {
			return this.right;
		}
		public int getKey() {
			return this.key;
		}
		public String getInfo() {
			return this.info;
		}
		public int getRank() {
			return this.rank;
		}
		public WAVLNode getParent() {
			return this.parent;
		}
	}

	public WAVLNode getRoot() {
		return this.root;
	}
	
	
	

}
