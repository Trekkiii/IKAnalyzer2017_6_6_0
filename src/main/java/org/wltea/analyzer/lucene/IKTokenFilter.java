package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

/**
 * Created by 刘春龙 on 2017/9/12.
 */
public class IKTokenFilter extends TokenFilter {

    private static final String SINGLE = "SINGLE";

    private boolean useSingle;// 是否对英文和数字单字分词
    private boolean useItself;// 是否保留英文和数字原语汇单元

    private Stack<Pair<Character, Integer>> synonymStack;// 同义词缓冲区
    private AttributeSource.State current;// 当前语汇单元状态

    //词元文本属性
    private final CharTermAttribute termAtt;
    //词元位移属性
    private final OffsetAttribute offsetAtt;
    //词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
    private final TypeAttribute typeAtt;
    private final PositionIncrementAttribute posIncrAtt;

    /**
     * Construct a token stream filtering the given input.
     *
     * @param input
     * @param useSingle
     * @param useItself
     */
    public IKTokenFilter(TokenStream input, boolean useSingle, boolean useItself) {
        super(input);
        this.useSingle = useSingle;
        this.useItself = useItself;

        synonymStack = new Stack<>();

        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    /**
     * 一次只能输出一个语汇单元
     *
     * @return 同义词语汇单元
     * @throws IOException
     */
    public boolean incrementToken() throws IOException {

        if (synonymStack.size() > 0) {// 输出缓冲区中的同义词语汇单元
            Pair<Character, Integer> currPair = synonymStack.pop();
            restoreState(current);
            termAtt.copyBuffer(new char[]{currPair.getKey()}, 0, 1);
            int startOffset = offsetAtt.startOffset() + currPair.getValue();
            offsetAtt.setOffset(startOffset, startOffset + 1);
            posIncrAtt.setPositionIncrement(1);
            typeAtt.setType(SINGLE);
            return true;
        }

        // 读取下一个语汇单元
        // 如果失败，直接返回false
        if (!input.incrementToken())
            return false;

        if (!useSingle) {
            return false;
        }

        // 同义词入栈
        // 入栈成功，则保存当前语汇单元状态，用于后续的同义词语汇单元输出
        if (addAliasesToStack()) {

            if (!useItself) {
                Pair<Character, Integer> currPair = synonymStack.pop();
                termAtt.copyBuffer(new char[]{currPair.getKey()}, 0, 1);
                int startOffset = offsetAtt.startOffset() + currPair.getValue();
                offsetAtt.setOffset(startOffset, startOffset + 1);
                posIncrAtt.setPositionIncrement(1);
                typeAtt.setType(SINGLE);
            }

            current = captureState();
        }
        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String type = typeAtt.type();
        // 类型为英文或数字
        if (type == null || (!type.equals("ENGLISH") && !type.equals("ARABIC"))) {
            return false;
        }

        final char[] synonyms = new String(termAtt.buffer(), 0, termAtt.length()).toCharArray();
        if (synonyms.length == 0) {
            return false;
        }

        for (int i = synonyms.length - 1; i >= 0; i--) {// 入栈
            char synonym = synonyms[i];
            if (synonym != '\u0000') {
                synonymStack.push(new Pair<>(synonym, i));
            }
        }

        return true;
    }

    protected class Pair<K, V> implements Serializable {

        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
