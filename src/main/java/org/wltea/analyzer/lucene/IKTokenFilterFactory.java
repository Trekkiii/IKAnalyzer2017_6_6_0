package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * Created by 刘春龙 on 2017/9/12.
 */
public class IKTokenFilterFactory extends TokenFilterFactory {

    private boolean useSingle;
    private boolean useItself;
    /**
     * Initialize this factory via a set of key-value pairs.
     *
     * 从{@code managed-schema}传递的值中。设置 useSingle 的值
     *
     * @param args
     */
    public IKTokenFilterFactory(Map<String, String> args) {
        super(args);

        /*
         * 判断Map容器中是否存在useSingle，如果有获取该key对应的value。
         * 如果没有,则设置默认值，也就是第三个参数 false
         */
        useSingle = this.getBoolean(args, "useSingle", false);// 执行完，useSingle会被从map移除

        useItself = this.getBoolean(args, "useItself", true);// IKTokenFilter保留IKTokenizer输出的英文和数字原语汇单元

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenStream create(TokenStream input) {
        return new IKTokenFilter(input, useSingle, useItself);
    }
}
