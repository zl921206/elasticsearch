package com.kamluen.elasticsearch.common;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhanglei
 * @version 1.0.0
 * @ClassName: Page
 * @Description: 分页处理
 * @date 2018年11月05日 上午11:23:39
 */
public class Page<T> extends MutualInfo implements Serializable {

    private static final long serialVersionUID = -3537996905877531597L;

    public static final int DEFAULT_SIZE = 10;

    private Integer currentPage = 1;// 当前页
    private Integer totalPage; // 总页数
    private Integer rowsPerPage;// 每页数据行数
    private Integer totalRows; // 总记录数
    private List<T> list; // 数据集

    private int visualSize = 4;// 分页中距当前页的可见页数

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getRowsPerPage() {
        if (null == rowsPerPage) {
            this.rowsPerPage = DEFAULT_SIZE;
        }
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getVisualSize() {
        return visualSize;
    }

    public void setVisualSize(int visualSize) {
        this.visualSize = visualSize;
    }

    public static <T> Page<T> initPage(List<T> list, int totalRows, int rowsPerPage, int currentPage) {
        // 分页查询
        Page<T> page = new Page<T>();
        int pageNum = totalRows / rowsPerPage;
        if (totalRows % rowsPerPage == 0) {
            page.setTotalPage(pageNum);
        } else {
            page.setTotalPage(pageNum + 1);
        }
        page.setCurrentPage(currentPage);
        page.setTotalRows(totalRows);
        page.setList(list);
        page.setRowsPerPage(rowsPerPage);
        return page;
    }
}
