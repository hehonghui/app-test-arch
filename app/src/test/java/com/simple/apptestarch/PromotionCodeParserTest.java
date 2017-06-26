package com.simple.apptestarch;

import com.simple.apptestarch.utils.PromotionCodeParser;

import junit.framework.TestCase;

import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * 不依赖于 Android Framework的单元测试,这类测试运行速度很快.
 * Created by mrsimple on 8/8/16.
 */
public class PromotionCodeParserTest extends TestCase {

    private static final String MOCK_REFERRER_PROM_CODE = "utm_source=pt&promote_code=b6ven8";
    private static final String MOCK_REFERRER_INVALID_PROM_CODE = "utm_source=pt&promote_code=";
    private static final String MOCK_REFERRER_NO_PROM_CODE = "utm_source=pt";


    /**
     * 测试有 promotion code的情况
     * @throws Exception
     */
    @Test
    public void testPromotionCode() throws Exception {
        PromotionCodeParser spyParser = spy(new PromotionCodeParser());
        // 模拟 promotion_code
        when(spyParser.getReferrer()).thenReturn(MOCK_REFERRER_PROM_CODE);
        assertEquals("b6ven8", spyParser.parsePromotionCode());
    }

    /**
     * 测试有 promote_code 的键,但是没有值
     * @throws Exception
     */
    @Test
    public void testInvalidPromotionCode() throws Exception {
        PromotionCodeParser spyParser = spy(new PromotionCodeParser());
        // 模拟 promotion_code
        when(spyParser.getReferrer()).thenReturn(MOCK_REFERRER_INVALID_PROM_CODE);
        assertEquals("", spyParser.parsePromotionCode());
    }

    /**
     * 测试 没有promote_code 的键,也没有值
     * @throws Exception
     */
    @Test
    public void testNoPromotionCode() throws Exception {
        PromotionCodeParser spyParser = spy(new PromotionCodeParser());
        // 模拟 promotion_code
        when(spyParser.getReferrer()).thenReturn(MOCK_REFERRER_NO_PROM_CODE);
        assertEquals("", spyParser.parsePromotionCode());
    }


    /**
     * 测试 referrer 为空
     * @throws Exception
     */
    @Test
    public void testNullReferrer() throws Exception {
        PromotionCodeParser spyParser = spy(new PromotionCodeParser());
        // 模拟 promotion_code
        when(spyParser.getReferrer()).thenReturn(null);
        assertEquals("", spyParser.parsePromotionCode());
    }
}
