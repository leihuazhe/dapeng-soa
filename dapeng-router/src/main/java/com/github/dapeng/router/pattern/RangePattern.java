package com.github.dapeng.router.pattern;

/**
 * 描述: 范围条件表达式
 *
 * @author hz.lei
 * @date 2018年04月13日 下午9:40
 */
public class RangePattern implements Pattern {
    public final long from;
    public final long to;

    public RangePattern(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "RangePattern{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
