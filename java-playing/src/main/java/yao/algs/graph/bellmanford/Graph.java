package yao.algs.graph.bellmanford;

import yao.algs.list.TwoWayList;

import java.util.Arrays;

/**
 * Bellman-Ford算法实现的单源最短路径
 * <p>
 * 适用于--有向图--，适用于--有环图--，权值(边)可以为负
 * 与顶点数据内容无关，只需顶点数组下标
 * <p>
 * 如果存在一个从源结点可以到达的权重为负值的环路，则算法将还会告诉我们不存在解决方案
 * Bellman-Ford算法就是对每一条边进行|V|-1次的松弛操作
 * <p>
 * Created by yaoo on 1/16/18
 */
@SuppressWarnings("unused")
public class Graph {

    private int vlen;
    private int elen;
    //所有邻接表的头节点
    private TwoWayList<Edge>[] edgeLinks;

    //所有顶点到起始点的最短距离
    private int[] distance;
    //该顶点的前驱顶点，用于松弛操作
    private int[] prev;


    //无穷大
    public static final int INFINITY_MAX = Integer.MAX_VALUE;
    //不存在
    public static final int NA = -1;

    @SuppressWarnings("unchecked")
    public Graph(int vlen) {
        this.vlen = vlen;
        this.elen = 0;
        this.edgeLinks = new TwoWayList[vlen];
        for (int i = 0; i < vlen; i++) {
            edgeLinks[i] = new TwoWayList<>();
        }

        this.distance = new int[vlen];
        this.prev = new int[vlen];
    }

    /**
     * 带权值的边 类
     */
    class Edge {
        int v1;
        int v2;
        int weight;

        public Edge(int v1, int v2, int weight) {
            this.v1 = v1;
            this.v2 = v2;
            this.weight = weight;
        }

        public Edge(int weight) {
            this(-1, -1, weight);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            Edge edge = (Edge) obj;
            return this.v1 == edge.v1 && this.v2 == edge.v2 && this.weight == edge.weight;
        }

        @Override
        public String toString() {
            return "Edge{" + "v1=" + v1 + ", v2=" + v2 + ", weight=" + weight + '}';
        }
    }

    public void addEdge(Edge edge) {
        edgeLinks[edge.v1].add(edge);
        elen++;
    }

    public void addEdge(int v1, int v2, int weight) {
        Edge edge = new Edge(v1, v2, weight);
        addEdge(edge);
    }

    /**
     * 计算从起始顶点start到其余各顶点的最短路径
     *
     * @param start 起始顶点位置下标
     * @return 是否有负环
     */
    @SuppressWarnings("unchecked")
    public boolean bellmanFordShortestPath(int start) {

        initStartNode(start);

        ///计算各顶点到起始点距离
        for (int i = 0; i < vlen - 1; i++) {
            for (int j = 0; j < vlen; j++) {
                TwoWayList<Edge> list = (TwoWayList<Edge>) edgeLinks[j].clone();
                while (!list.isEmpty()) {
                    Edge edge = list.removeLast();
                    relaxEdge(edge);
                }
            }
        }

        ///添加判断负环的操作
        for (int i = 0; i < vlen; i++) {
            TwoWayList<Edge> list = (TwoWayList<Edge>) edgeLinks[i].clone();
            while (!list.isEmpty()) {
                Edge edge = list.removeLast();
                if (distance[edge.v2] > distance[edge.v1] + edge.weight) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * 初始化所有顶点到起始点的最短距离数组
     *
     * @param start 起始顶点
     */
    public void initStartNode(int start) {
        //到起始点的距离都设为无穷大
        Arrays.fill(distance, INFINITY_MAX);
        //某顶点的前驱节点，不一定存在
        Arrays.fill(prev, NA);
        //到自身的距离记为0
        distance[start] = 0;
    }

    /**
     * 从待选顶点中选出距离最小的边的另一顶点，并删除这条边
     *
     * @param u 未求出最短路径的的顶点集合
     * @return 最小距离
     */
    public int getMinDsitance(TwoWayList<Integer> u) {

        int min = u.get(0);
        for (int i = 0; i < u.size(); i++) {
            int v = u.get(i);
            if (distance[v] < distance[min]) {
                min = v;
            }
        }

        return min;
    }

    /**
     * 松弛某一顶点的邻接点
     *
     * @param edge 边
     */
    public void relaxEdge(Edge edge) {

        if (distance[edge.v2] > distance[edge.v1] + edge.weight) {
            //更新距离值
            distance[edge.v2] = distance[edge.v1] + edge.weight;
            //记录前驱节点
            prev[edge.v2] = edge.v1;
        }

    }

    public void printShortestPath() {

        System.out.println("终点" + "\t" + "最短路径" + "\t" + "路线");

        //各顶点到起始点的路径
        TwoWayList<Integer>[] routes = (TwoWayList<Integer>[]) new TwoWayList[vlen];

        for (int i = 0; i < vlen; i++) {
            routes[i] = new TwoWayList<>();

            //第i个节点到起点的路径入栈
            int j = i;
            while (j != NA) {
                routes[i].addLast(j);
                j = prev[j];
            }

            System.out.print(i + "\t" + distance[i] + "\t\t");
            //以出栈顺序打印第i个节点的路径
            while (!routes[i].isEmpty()) {
                int c = routes[i].removeLast();
                System.out.print(" --> " + c);
            }
            System.out.println();
        }
    }

    @SuppressWarnings("unchecked")
    public void printGraph() {
        System.out.println("图的顶点有：" + vlen + " 个，边有：" + elen + " 条");
        System.out.println("顶点索引" + "\t" + "邻接点列表[权值]");

        for (int i = 0; i < vlen; i++) {
            System.out.print(i + "\t\t");

            TwoWayList<Edge> stack = (TwoWayList<Edge>) edgeLinks[i].clone();
            while (!stack.isEmpty()) {

                Edge edge = stack.removeLast();
                System.out.print(edge.v2 + "[" + edge.weight + "]" + "\t");
            }
            System.out.println();
        }
    }
}
