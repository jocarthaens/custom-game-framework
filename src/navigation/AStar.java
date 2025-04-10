package navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

// Abstract class for finding the shortest path/cost to goal state that can be used in various ways

public abstract class AStar<T> {
	protected PriorityQueue<AStarNode<T>> openSet;
	protected Map<T, AStarNode<T>> nodeList;
	protected Stack<AStarNode<T>> reservedNodes;
	
	
	public AStar() {
		reservedNodes = new Stack<AStarNode<T>>();
		nodeList = new HashMap<T, AStarNode<T>>();
		openSet = new PriorityQueue<AStarNode<T>>(new Comparator<AStarNode<T>>() {
            @Override
            public int compare(AStarNode<T> n1, AStarNode<T> n2) {
                int result = Float.compare(n1.fScore, n2.fScore); // prioritize nodes with lower fScore
                if (result == 0) {
                	result = (-1) * Float.compare(n1.gScore, n2.gScore); // prioritize nodes with higher gScore
                }
                return result;
            }
		});
	}
	
	
	
	public List<T> searchPath(T start, T goal) {
		AStarNode<T> startNode = provideNode(start, 0, getHeuristic(start, goal), null);
		addToNodeList(startNode);
		addToOpenSet(startNode);
		
		AStarNode<T> current = startNode;
		int iterationCount = 0;
		
		while (!openSet.isEmpty() && iterationCount < iterationLimit()) {
			iterationCount += 1;
			current = popOpenSet();
			
			if (isGoal(current.state, goal)) {
				return reconstructSuccess(current);
			}
			
			for (T neighbor: getNeighbors(current.state)) {
				AStarNode<T> neighborNode = getFromNodeList(neighbor);
				float newGScore = current.gScore 
						+ getTraversalCost(current.state, neighbor);
				if (neighborNode == null) {
					neighborNode = provideNode(neighbor, newGScore, 
							newGScore + getHeuristic(neighbor, goal), current);
					addToNodeList(neighborNode);
					addToOpenSet(neighborNode);
				}
				else if (newGScore < neighborNode.gScore) {
					neighborNode.gScore = newGScore;
					neighborNode.fScore = newGScore + getHeuristic(neighbor, goal);
					neighborNode.precursor = current;
					// reinsert node to update priority
					removeFromOpenSet(neighborNode);
					addToOpenSet(neighborNode);
				}				
			}
			
			
		}
		
		return reconstructFailed(current);
	}
	
	/**
	 *  Method for providing the list of elements if the search for shortest path
	 *  is a success. Can be overriden for things like post-smoothing.
	 */
	protected List<T> reconstructSuccess(AStarNode<T> node) {
		List<T> list = new ArrayList<T>();
		for (AStarNode<T> current = node; current != null; current = current.precursor) {
			list.add(current.state);
		}
        Collections.reverse(list);
		clear();
		return list;
	}
	
	/**
	 *  Overridable method for providing the list of elements
	 *  given that the search failed to find the shortest path from start to goal.
	 *  Default implementation returns an empty list.
	 */
	protected List<T> reconstructFailed(AStarNode<T> node) {
		clear();
		return Collections.emptyList();
	}
	
	/**
	 *  Overridable method for limiting the number of iterations
	 *  in searching for the shortest path. 
	 *  Default implementation returns -1 for infinite iterations.
	 */
	protected int iterationLimit() {
		return -1;
	}
	

	
	
	protected abstract Iterable<T> getNeighbors(T node);
	
	protected abstract float getTraversalCost(T from, T to);
	
	protected abstract float getHeuristic(T current, T goal);
	
	protected abstract boolean isGoal(T current, T goal);
	
	
	
	
	
	protected void addToOpenSet(AStarNode<T> node) {
		openSet.add(node.setOpen(true));
	}
	
	protected void removeFromOpenSet(AStarNode<T> node) {
		openSet.remove(node.setOpen(false));
	}
	
	protected AStarNode<T> popOpenSet() {
		return openSet.poll().setOpen(false);
	}
	
	protected boolean inOpenSet(T node) {
		AStarNode<T> star = nodeList.get(node);
		return star != null && star.isOpen == true;
	}
	
	
	
	protected void addToNodeList(AStarNode<T> node) {
		nodeList.putIfAbsent(node.state, node);
	}
	
	protected AStarNode<T> getFromNodeList(T node) {
		return nodeList.get(node);
	}
	
	
	
	protected void clear() {
		openSet.clear();
		for (AStarNode<T> node: nodeList.values()) {
			returnNode(node);
		}
		nodeList.clear();
	}
	

	
	
	
	protected AStarNode<T> provideNode(T state, float gScore, float fScore, AStarNode<T> parent) {
		if (reservedNodes.isEmpty()) {
			AStarNode<T> newNode = new AStarNode<T>(state, gScore, fScore, parent);
			return newNode;
		}
		return reservedNodes.pop().setPrecursor(parent)
				.setFScore(fScore).setGScore(fScore).setState(state);
	}
	
	protected void returnNode(AStarNode<T> node) {
		node.state = null;
		node.precursor = null;
		node.gScore = 0;
		node.fScore = 0;
		node.isOpen = false;
		reservedNodes.add(node);
	}
	
	protected void clearCache() {
		reservedNodes.clear();
	}
	
	
	
	
	
	
	
	
	
	
	protected static class AStarNode<T> {
		protected T state;
		protected float gScore;
		protected float fScore;
		protected AStarNode<T> precursor;
		protected boolean isOpen;
		
		protected AStarNode(T state, float gScore, float fScore, AStarNode<T> parent) {
			this.state = state;
			this.precursor = parent;
			this.gScore = gScore;
			this.fScore = fScore;
		}
		
		public AStarNode<T> setState(T state) {
			this.state = state;
			return this;
		}
		
		public AStarNode<T> setGScore(float score) {
			this.gScore = score;
			return this;
		}
		
		public AStarNode<T> setFScore(float score) {
			this.fScore = score;
			return this;
		}
		
		public AStarNode<T> setPrecursor(AStarNode<T> precursor) {
			this.precursor = precursor;
			return this;
		}
		
		public AStarNode<T> setOpen(boolean isOpen) {
			this.isOpen = isOpen;
			return this;
		}
	}
	
}
