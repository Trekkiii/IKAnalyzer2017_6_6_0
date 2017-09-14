/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * IK分析器，Lucene Analyzer接口实现
 * <p>
 * 兼容Lucene 6.6.0 版本
 * <p>
 * Modified by 刘春龙 on 2017/8/29.
 */
public final class IKAnalyzer extends Analyzer {

    private boolean useSmart;
    private boolean useSingle;
    private boolean useItself;

    public boolean useSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public boolean useSingle() {
        return useSingle;
    }

    public void setUseSingle(boolean useSingle) {
        this.useSingle = useSingle;
    }

    public boolean useItself() {
        return useItself;
    }

    public void setUseItself(boolean useItself) {
        this.useItself = useItself;
    }

    /**
     * IK分析器Lucene  Analyzer接口实现类
     * <p>
     * 默认细粒度切分算法
     */
    public IKAnalyzer() {
        this(false, false, false);
    }

    /**
     * IK分析器Lucene Analyzer接口实现类
     *
     * @param useSmart  当为true时，分词器进行智能切分
     * @param useSingle 是否针对英文和数字做单字切分
     * @param useItself 是否保留英文和数字原语汇单元
     */
    public IKAnalyzer(boolean useSmart, boolean useSingle, boolean useItself) {
        super();
        this.useSmart = useSmart;
        this.useSingle = useSingle;
        this.useItself = useItself;
    }

    /**
     * 重载Analyzer接口，构造分词组件
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
        IKTokenFilter _IKTokenFilter = new IKTokenFilter(_IKTokenizer, useSingle, useItself);
        return new TokenStreamComponents(_IKTokenizer, _IKTokenFilter);
    }

}
