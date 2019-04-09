package cn.ges.aliasrandom.core;


import cn.ges.aliasrandom.aliasmethod.AliasMethod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 快速计算概率
 *
 * 该类是线程安全的,可以多个线程共用一个对象,但是需要注意 -> 输入完毕概率后先调用一次getResult或者init方法
 * 如果这个类的概率会重复使用 请使用单例模式 同时和上一条一样 输入完毕概率后先调用一次getResult或者init方法
 * @author gespent@163.com
 * @date 2019/2/20
 */
public class Probability<T> {
    private AliasMethod aliasMethod;
    private final ArrayList<Double> odds;
    private final HashMap<Integer, T> results;
    private Integer count = 0;
    private Double sum = 0D;
    private boolean alreadyGot = false;

    public Probability() {
        odds = new ArrayList<>(10);
        results = new HashMap<>(10);
    }

    /**
     * 已知元素 数目的条件下 使用该方法来创建Probability
     * @param elementQuantity 元素数目
     */
    public Probability(int elementQuantity) {
        odds = new ArrayList<>(elementQuantity);
        results = new HashMap<>(elementQuantity);
    }

    /**
     * 添加一种概率可能 当指向该概率时 会返回此时设置的result<br></>
     * 最后获取结果时，如果总概率不到1会自动补正到1,此时result为null<br></>
     * 当已经调用过getResult方法后 该方法将不会继续<br></>
     * @param probability 概率 0<probability<1 概率和应该小于1
     * @param result      指向该概率时 返回值
     * @return 操作的Probability对象
     */
    public Probability<T> addElement(Double probability, T result) {
        if (alreadyGot) {
            return this;
        }
        odds.add(probability);
        results.put(count++, result);
        sum += probability;
        return this;
    }

    public void init() {
        checkOdds();
    }
    /**
     * 获取结果前 检查概率是否有问题.并补正不到1的部分
     */
    private void checkOdds() {
        if (this.sum == 1) {
            this.alreadyGot = true;
        } else if (this.sum < 1) {
            this.addElement(1D - this.sum, null);
            this.sum = 1D;
            this.alreadyGot = true;
        } else {
            throw new IllegalArgumentException("概率和不应该大于1!");
        }
    }

    /**
     * 获得结果<br></>
     * 如果概率和不为1 随机到不为1的部分将返回null
     */
    public T getResult() {
        if (!alreadyGot) {
            checkOdds();
        }
        if (this.aliasMethod == null) {
            final String name = Thread.currentThread().getName();
            System.out.println(name + "创建了aliasMethod");
            this.aliasMethod = new AliasMethod(odds);
        }
        return results.get(this.aliasMethod.next());
    }


}
