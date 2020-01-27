package assignments;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.awt.Point;

public class homework {
	String type;
	int columns;
	int rows;
	Point  source;
	int elevation;
	int noOfTargets;
	ArrayList<Point> targetSites;
	List<List<Integer>> list;
	class UcsNode {
		int pathCost;
		int x;
		int y;
		UcsNode parent;
		int level;
		public UcsNode (int x,int y, int cost, UcsNode parent, int level) {
			this.pathCost = cost;
			this.x = x;
			this.y = y;
			this.parent = parent;
			this.level = level;
		}
	}
	class AstarNode {

		int x;
		int y;
		AstarNode parent;
		int actualPathCost;//g
		double estimatedPathCost;//h
		double totalPathCost;//f= g+h
		public AstarNode(int x, int y, AstarNode node,int actualCost, double estimatedCost) {
			this.x = x;
			this.y = y;
			this.parent = node;
			this.actualPathCost = actualCost;
			this.estimatedPathCost = estimatedCost;
			this.totalPathCost = actualCost+estimatedCost;
		}
	}
	public homework() {
		this.type ="";
		this.source = new Point();
		targetSites = new ArrayList<>();
		list = new ArrayList<>();
	}
	

	public static void main(String[] args) {
		File file = new File("/Users/akankshapriya/AI_Assignments/Homework1/src/input1.txt");
		try {
			Scanner sc = new Scanner(file);
			homework mars = new homework();

			if(sc.hasNextLine()) {
				mars.type = sc.next();
			}
			if(sc.hasNextLine()) {
				mars.columns = Integer.parseInt(sc.next());
			}
			if(sc.hasNextLine()) {
				mars.rows = Integer.parseInt(sc.next());
			}
			if(sc.hasNextLine()) {
				mars.source.y = Integer.parseInt(sc.next());
			}
			if(sc.hasNextLine()) {
				mars.source.x = Integer.parseInt(sc.next());
			}
			if(sc.hasNextLine()) {
				mars.elevation = Integer.parseInt(sc.next());
			}
			if(sc.hasNextLine()) {
				mars.noOfTargets = Integer.parseInt(sc.next());
			}
			int n = mars.noOfTargets;
			while(n>0) {
				Point pt = new Point();
				if(sc.hasNextLine()) {
					pt.y = Integer.parseInt(sc.next());
				}
				if(sc.hasNextLine()) {
					pt.x = Integer.parseInt(sc.next());
				}
				mars.targetSites.add(pt);
				n--;
			}
			int[][] matrix = new int [mars.columns][ mars.rows];
			
			for(int i =0; i<mars.rows;i++) {
				for(int j =0; j<mars.columns;j++) {
					matrix[j][i]= Integer.parseInt(sc.next());
					
				}
				
			}		
			List<List<Point>> shortestPath = new ArrayList<>();
			List<List<String>> optimalPath = new ArrayList<>();
			switch(mars.type) {
				case "BFS":
					
					for(Point p: mars.targetSites) {
						List<Point> res = mars.triggerBFS(mars, matrix, p);
						shortestPath.add(res);
					}
					mars.processToOutput(shortestPath);
					break;
				case "UCS":
					
					for(Point p: mars.targetSites) {
						List<String> res= mars.triggerUCS(mars,matrix,p);
						optimalPath.add(res);
					}
					mars.printOutput(optimalPath);
					break;
				case "A*":
					
					for(Point p: mars.targetSites) {
						List<String> res =mars.triggerAstar(mars, matrix, p);
						optimalPath.add(res);
					}
					mars.printOutput(optimalPath);
					break;
				default:
					//System.out.println("def");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	
	public void printOutput(List<List<String>> res) {
		FileWriter writer;
		try {
			writer = new FileWriter("/Users/akankshapriya/AI_Assignments/Homework1/src/output.txt");
			int size = res.size();
			for(int j = 0; j<size; j++) {
				int len = res.get(j).size();
				if(len == 0) {
					writer.write("FAIL");
					if(j != (size-1)) writer.write("\r\n");
				}else {
					int l = res.get(j).size();
						for(int i =0; i<l;i++) {
							if(i != l-1) {
								writer.write(res.get(j).get(i)+" ");
							}else {
								writer.write(res.get(j).get(i));
							}
						}
						
						if(j != (size-1)) writer.write("\r\n");
					}
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		
		
	}
	public void processToOutput(List<List<Point>> res) {
		FileWriter writer;
		try {
			writer = new FileWriter("/Users/akankshapriya/AI_Assignments/Homework1/src/output.txt");
			int size = res.size();
			for(int j = 0; j<size; j++) {
				int len = res.get(j).size();
				if(len == 0) {
					writer.write("FAIL");
					if(j != (size-1)) writer.write("\r\n");
				}else {
					int l = res.get(j).size();
						for(int i =0; i<l;i++) {
							if(i == len-1) {
								writer.write(res.get(j).get(i).y+","+res.get(j).get(i).x);
							}else {
								writer.write(res.get(j).get(i).y+","+res.get(j).get(i).x+" ");
							}
							
						}
						if(j != (size-1))writer.write("\r\n");
					}
				}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		
		
	}
	
	
	public List<Point> triggerBFS(homework mars, int[][]matrix, Point dest) {


		List<Point> path = new ArrayList<>();
		boolean pathExists = false;
		HashMap<Point, Point> parentMap = new HashMap<>();
		Queue<Point> q = new LinkedList<>(); 
		q.add(source);
		boolean[][]visited = new boolean[mars.rows][mars.columns];
		visited[source.x][source.y] = true;
		parentMap.put(source,null);
		
		Point pt = null;
		while(!q.isEmpty()) {
			pt = q.poll();
			if(dest.x == pt.x && dest.y == pt.y) {
				pathExists = true;
				break;
			}
			int x = pt.x;
			int y = pt.y;
			int val = matrix[pt.y][pt.x];
			//get all valid neighbours
			if(mars.isValid(mars, y-1, x-1) && visited[x-1][y-1]==false
					&& (Math.abs(matrix[y-1][x-1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x-1,y-1),pt);
				visited[x-1][y-1]=true;
				q.add(new Point(x-1,y-1));
			}
			if(mars.isValid(mars, y-1, x) && visited[x][y-1]==false 
					&& (Math.abs(matrix[y-1][x]-val)<= mars.elevation) ) {
				parentMap.put(new Point(x,y-1),pt);
				visited[x][y-1]=true;
				q.add(new Point( x, y-1));
			}
			if(mars.isValid(mars, y-1, x+1)&& visited[x+1][y-1]==false 
					&& (Math.abs(matrix[y-1][x+1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x+1, y-1),pt);
				visited[x+1][y-1]=true;
				q.add(new Point( x+1, y-1));
			}
			if(mars.isValid(mars, y, x-1) && visited[x-1][y]==false
					&& (Math.abs(matrix[y][x-1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x-1, y),pt);
				visited[x-1][y]= true;
				q.add(new Point( x-1,y));
			}
			if(mars.isValid(mars, y, x+1)&& visited[x+1][y]==false
					&& (Math.abs(matrix[y][x+1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x+1,y),pt);
				visited[x+1][y]=true;
				q.add(new Point( x+1,y));
			}
			if(mars.isValid(mars, y+1, x-1)&& visited[x-1][y+1]==false
					&& (Math.abs(matrix[y+1][x-1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x-1,y+1),pt);
				visited[x-1][y+1] = true;
				q.add(new Point( x-1, y+1));
			}
			if(mars.isValid(mars, y+1, x)&& visited[x][y+1]==false
					&& (Math.abs(matrix[y+1][x]-val)<= mars.elevation)) {
				parentMap.put(new Point(x, y+1),pt);
				visited[x][y+1] = true;
				q.add(new Point( x, y+1));
			}
			if(mars.isValid(mars, y+1, x+1)&& visited[x+1][y+1]==false
					&& (Math.abs(matrix[y+1][x+1]-val)<= mars.elevation)) {
				parentMap.put(new Point(x+1, y+1),pt);
				visited[x+1][y+1] = true;
				q.add(new Point( x+1, y+1));
			}
				
		}
		if(pathExists) {
			Point n = pt;
			while(n != null) {
				path.add(n);
				n = parentMap.get(n);
			}
			Collections.reverse(path);
		}
		
		return path;
	}
	
	public List<String> triggerUCS(homework mars, int[][]matrix, Point dest) {
		UcsNode[][]visited = new UcsNode[mars.rows][mars.columns];
		boolean pathExists = false;
		HashMap<String, UcsNode> ucsMap= new HashMap<>();
		PriorityQueue<UcsNode> queue = new PriorityQueue<UcsNode>(new Comparator<UcsNode>(){
					public int compare(UcsNode i, UcsNode j){
	                    if(i.pathCost > j.pathCost){
	                        return 1;
	                    }
	                    else if (i.pathCost < j.pathCost){
	                        return -1;
	                    }
	                    else{
	                        return 0;
	                    }
	                }
	            });
		UcsNode node = new UcsNode(mars.source.x,mars.source.y, 0, null, 0);
		queue.add(node);
		ucsMap.put(node.x+","+node.y, node);
		UcsNode n = null;
		while(!queue.isEmpty()) {
			n = queue.poll();
			if(dest.x == n.x && dest.y == n.y) {
				pathExists = true;
				break;
			}
			int x = n.x;
			int y = n.y;
			int val = matrix[y][x];
			if(mars.isValid(mars, y-1, x-1)&& (Math.abs(matrix[y-1][x-1]-val)<= mars.elevation)) {
				
				helperUCS(y-1, x-1, matrix, 14, dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars, y,x-1) && (Math.abs(matrix[y][x-1]-val)<= mars.elevation)) {
				helperUCS(y, x-1, matrix, 10,dest,n, queue, visited, n.level,ucsMap);
			}
			if(mars.isValid(mars, y+1, x-1) 
					&& (Math.abs(matrix[y+1][x-1]-val)<= mars.elevation)) {
				helperUCS(y+1, x-1, matrix, 14,dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars,y-1, x) && (Math.abs(matrix[y-1][x]-val)<= mars.elevation)) {
				helperUCS(y-1, x, matrix, 10,dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars, y+1,x) && (Math.abs(matrix[y+1][x]-val)<= mars.elevation)) {
				helperUCS(y+1, x, matrix, 10,dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars, y-1, x+1) && (Math.abs(matrix[y-1][x+1]-val)<= mars.elevation)) {
				helperUCS(y-1, x+1, matrix, 14,dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars, y,x+1) && (Math.abs(matrix[y][x+1]-val)<= mars.elevation)) {
				helperUCS(y, x+1, matrix, 10,dest,n, queue, visited, n.level, ucsMap);
			}
			if(mars.isValid(mars, y+1,x+1) && (Math.abs(matrix[y+1][x+1]-val)<= mars.elevation)) {
				helperUCS(y+1, x+1, matrix, 14,dest,n, queue, visited, n.level, ucsMap);
			}
			visited[n.x][n.y]=n;
			ucsMap.remove(n.x+","+n.y);
			
		}
		List<String> path = new ArrayList<>();
		if(pathExists) {
			System.out.println(n.pathCost);
			while(n!=null) {
				path.add(n.y+","+n.x);
				n=n.parent;
			}
			Collections.reverse(path);	
		}
		return path;
	}
	
	public List<String> triggerAstar(homework mars, int[][] matrix, Point dest){


		AstarNode[][]visited = new AstarNode[mars.rows][mars.columns];
		boolean pathExists = false;
		HashMap<String, AstarNode> aStarMap= new HashMap<>();
		PriorityQueue<AstarNode> queue = new PriorityQueue<AstarNode>(new Comparator<AstarNode>(){

            public int compare(AstarNode i, AstarNode j){
                if(i.totalPathCost > j.totalPathCost){
                    return 1;
                }
                else if (i.totalPathCost < j.totalPathCost){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
		
		AstarNode node = new AstarNode(mars.source.x,mars.source.y,null, 0, 
				calculateDistance(mars.source.x,mars.source.y, dest));
		queue.add(node);
		aStarMap.put(node.x+","+node.y,node);
		AstarNode n = null;
		while(!queue.isEmpty()) {
			n = queue.poll();
			if(dest.x == n.x && dest.y == n.y) {
				pathExists = true;
				break;
			}
			int x = n.x;
			int y = n.y;
			int val = matrix[y][x];
			if(mars.isValid(mars, y-1, x-1)&& (Math.abs(matrix[y-1][x-1]-val)<= mars.elevation)) {
				helper(x-1,y-1,matrix,dest,val,n,queue,visited, 14, aStarMap);
				
			}
			if(mars.isValid(mars, y,x-1) && (Math.abs(matrix[y][x-1]-val)<= mars.elevation)) {
				helper(x-1,y,matrix,dest,val,n,queue,visited, 10, aStarMap);
			}
			if(mars.isValid(mars, y+1, x-1) && (Math.abs(matrix[y+1][x-1]-val)<= mars.elevation)) {
				helper(x-1,y+1,matrix,dest,val,n,queue,visited, 14, aStarMap);
			}
			if(mars.isValid(mars,y-1, x) && (Math.abs(matrix[y-1][x]-val)<= mars.elevation)) {
				helper(x,y-1,matrix,dest,val,n,queue,visited, 10, aStarMap);
			}
			if(mars.isValid(mars, y+1,x) && (Math.abs(matrix[y+1][x]-val)<= mars.elevation)) {
				helper(x,y+1,matrix,dest,val,n,queue,visited, 10, aStarMap);
			}
			if(mars.isValid(mars, y-1, x+1)&& (Math.abs(matrix[y-1][x+1]-val)<= mars.elevation)) {
				helper(x+1,y-1,matrix,dest,val,n,queue,visited, 14, aStarMap);
			}
			if(mars.isValid(mars, y,x+1)&& (Math.abs(matrix[y][x+1]-val)<= mars.elevation)) {
				helper(x+1,y,matrix,dest,val,n,queue,visited, 10, aStarMap);
			}
			if(mars.isValid(mars, y+1,x+1) && (Math.abs(matrix[y+1][x+1]-val)<= mars.elevation)) {
				helper(x+1,y+1,matrix,dest,val,n,queue,visited, 14, aStarMap);
			}
			visited[x][y]=n;
			aStarMap.remove(n.x+","+n.y);
		}
		List<String> path = new ArrayList<>();
		
		if(pathExists) {
			System.out.println(n.actualPathCost);
			while(n!=null) {
				path.add(n.y+","+n.x);
				n=n.parent;
			}
			Collections.reverse(path);
			
		}
		return path;
		
	}
	
	public void helperUCS(int y, int x, int[][]matrix,int cost,Point dest, UcsNode n ,PriorityQueue<UcsNode> queue, 
			UcsNode[][] visited, int level, HashMap<String, UcsNode> map) {
		
		UcsNode n1 = new UcsNode(x,y, n.pathCost+cost , n, level+1);
		UcsNode p = null;
		if(map.containsKey(n1.x+","+n1.y)) {
			p = map.get(n1.x+","+n1.y);
			if(n1.pathCost<p.pathCost ) {
				queue.remove(p);
				queue.add(n1);
				map.put(n1.x+","+n1.y, n1);
				
			}
		}else if(visited[x][y]!= null) {
			p = visited[x][y];
			if(n1.pathCost< p.pathCost) {
				queue.add(n1);
				visited[x][y]=null;
			}
		}else {
			queue.add(n1);
			map.put(n1.x+","+n1.y, n1);
			
		}
	}
	
	
	public void helper(int x, int y, int[][]matrix, Point dest, int val, AstarNode n, PriorityQueue<AstarNode> queue, 
			AstarNode[][]visited, int pathcost, HashMap<String, AstarNode> map) {
		
		int elevationdiff = Math.abs(val- matrix[y][x]);
		AstarNode n1 = new AstarNode(x,y, n, n.actualPathCost+pathcost+elevationdiff , calculateDistance(x, y,dest));
		AstarNode p = null;
		if(map.containsKey(n1.x+","+n1.y)) {
			p = map.get(n1.x+","+n1.y);
			if(n1.totalPathCost<p.totalPathCost ) {
				queue.remove(p);
				queue.add(n1);
				map.put(n1.x+","+n1.y, n1);
			}
		}else if(visited[x][y]!= null) {
			p = visited[x][y];
			if(n1.totalPathCost< p.totalPathCost) {
				queue.add(n1);
				visited[x][y]=null;
			}
		}else {
			queue.add(n1);
			map.put(n1.x+","+n1.y, n1);
			
		}
	}
	
	
	public double calculateDistance (int x,int y, Point p2) {
		double ans = Math.sqrt(Math.pow(x - p2.x, 2)+ Math.pow(y - p2.y, 2));
		return ans;
	}
	public boolean isValid(homework mars, int y , int x) {
		if(x>=0 && x< mars.rows && y>=0 && y<mars.columns) {
			return true;
		}
		return false;
	}

}
