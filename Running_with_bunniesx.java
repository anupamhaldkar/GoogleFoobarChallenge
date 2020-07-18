import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Solution {

	class Path {
		ArrayList<Integer> nl = new ArrayList<Integer>();
		HashSet<Integer> ns = new HashSet<Integer>();
		int t; // timeSpent

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Path)) {
				return false;
			}

			Path p = (Path) o;

			return this.nl.equals(p.nl) && this.t == p.t;
		}

		public boolean isPathBetterThan(Path p) {
			if (this.t - times_limit <= 0 && p.t - times_limit > 0) {
				return true;
			} else if (p.t - times_limit <= 0 && this.t - times_limit > 0) {
				return false;
			}
			if (p.ns.size() > this.ns.size()) {
				return false;
			} else if (p.ns.size() < this.ns.size()) {
				return true;
			} else {
				for (int i = 0; i < hw; i++) {
					if (p.ns.contains(i) && !this.ns.contains(i)) {
						return false;
					} else if (this.ns.contains(i) && !p.ns.contains(i)) {
						return true;
					}
				}
			}
			return true;
		}
	}

	int[][] times;
	int times_limit;
	ArrayList<Path> pathList = new ArrayList<Path>(); // all paths
	ArrayList<Path> loopList = new ArrayList<Path>(); // loops
	int hw;

	public Solution(int[][] times, int times_limit) {
		this.times = times;
		this.times_limit = times_limit;
		this.hw = times.length;
	}

	public boolean loopAlreadyTraversed(Path p) {
		for (int i = 0; i < this.loopList.size(); i++) {
			if (this.loopList.get(i).equals(p)) {
				return true;
			}
		}
		return false;

	}

	// find all paths starting at row 0 to row hw-1, and all loops..
	public void buildLoopsAndPaths(LinkedHashSet<Integer> pathTillNow, int row) {
		for (int i = 0; i < hw; i++) {
			if (i != row) {
				if (pathTillNow.contains(i)) // add into loop
				{
					Path lp = new Path();
					boolean startAdd = false;
					for (Integer j : pathTillNow) {
						if (j == i) {
							startAdd = true;
						}

						if (startAdd) {
							lp.nl.add(j);
							lp.ns.add(j);
							if (lp.nl.size() > 1) {
								lp.t += times[lp.nl.get(lp.nl.size() - 2)][lp.nl.get(lp.nl.size() - 1)];
							}
						}
					}
					lp.nl.add(i);
					lp.t += times[lp.nl.get(lp.nl.size() - 2)][lp.nl.get(lp.nl.size() - 1)];

					if (!loopAlreadyTraversed(lp)) {
						loopList.add(lp);
					}
				} else {
					if (i == hw - 1) // add into path
					{
						pathTillNow.add(i);
						Path lp = new Path();
						lp.nl.addAll(pathTillNow);
						lp.ns.addAll(pathTillNow);
						for (int j = 1; j < lp.nl.size(); j++) {
							lp.t += times[lp.nl.get(j - 1)][lp.nl.get(j)];
						}
						pathList.add(lp);
					}
					@SuppressWarnings("unchecked")
					LinkedHashSet<Integer> subPath = (LinkedHashSet<Integer>) pathTillNow.clone();
					subPath.add(i);
					buildLoopsAndPaths(subPath, i);
				}
			}
		}
	}

	public void addLoopsToPath() {
		for (int i = 0; i < pathList.size(); i++) {
			for (int j = 0; j < loopList.size(); j++) {
				if (pathList.get(i).ns.contains(loopList.get(j).nl.get(0))
						&& (pathList.get(i).t + loopList.get(j).t - times_limit <= 0
								|| pathList.get(i).t + loopList.get(j).t <= pathList.get(i).t)) {
					@SuppressWarnings("unchecked")
					HashSet<Integer> pathPlusLoop = (HashSet<Integer>) pathList.get(i).ns.clone();
					pathPlusLoop.addAll(loopList.get(j).ns);

					if (pathPlusLoop.size() > pathList.get(i).ns.size()
							|| pathList.get(i).t + loopList.get(j).t < pathList.get(i).t) {
						Path pNew = new Path();
						pNew.ns = pathPlusLoop;
						pNew.t = pathList.get(i).t + loopList.get(j).t;
						pNew.nl.addAll(pathList.get(i).nl);
						int loopStartIndex = pNew.nl.indexOf(loopList.get(j).nl.get(0));
						pNew.nl.remove(loopStartIndex);
						pNew.nl.addAll(loopStartIndex, loopList.get(j).nl);
						pathList.add(pNew);
						if(pNew.ns.size() == hw && pNew.t - times_limit<=0) 
						{
							return;
						}
					}
				}
			}
		}
	}

	public int[] getBestPathPossible() {
		Path currBestPath = pathList.get(0);
		for (int i = pathList.size() - 1; i > 0; i--) {
			if (!currBestPath.isPathBetterThan(pathList.get(i))) {
				currBestPath = pathList.get(i);
			}
		}

		@SuppressWarnings("unchecked")
		HashSet<Integer> bestPathSet = (HashSet<Integer>) currBestPath.ns.clone();
		bestPathSet.remove(0);
		bestPathSet.remove(hw - 1);

		int[] ans = new int[bestPathSet.size()];
		int j = 0;
		for (Integer i : bestPathSet) {
			ans[j] = i - 1;
			j++;
		}
		return ans;
	}

	public int[] bestPathInCaseOfNegativeLoops() {
		int[] ans = new int[hw - 2];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = i;
		}
		return ans;
	}

	public int[] findSolution() {
		LinkedHashSet<Integer> pathTillNow = new LinkedHashSet<Integer>();
		pathTillNow.add(0);
		buildLoopsAndPaths(pathTillNow, 0);

		for (int i = 0; i < loopList.size(); i++) {
			if (loopList.get(i).t < 0) {
				return bestPathInCaseOfNegativeLoops();
			}
		}

		addLoopsToPath();

		return getBestPathPossible();
	}

	public static int[] solution(int[][] times, int times_limit) {
		if (times.length < 3) {
			return new int[0];
		}
		Solution s = new Solution(times, times_limit);
		return s.findSolution();
	}
}
