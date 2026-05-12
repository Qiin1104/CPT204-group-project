package algorithm;
import model.Location;

import java.util.List;
/**
 * 定义排序算法的通用接口
 */
public interface Sorter {
    // 返回算法名称，方便在 Task A 报告中输出表格 [cite: 128]
    String name();

    // 排序方法，接收原始列表，返回排序后的新列表
    List<Location> sort(List<Location> data);
}
