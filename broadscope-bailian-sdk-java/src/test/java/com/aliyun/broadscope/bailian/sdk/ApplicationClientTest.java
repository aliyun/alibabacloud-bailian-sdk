/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.alibaba.fastjson.JSON;
import com.aliyun.broadscope.bailian.sdk.consts.DocReferenceTypeEnum;
import com.aliyun.broadscope.bailian.sdk.models.*;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Title 对话客户端测试用例.<br/>
 * Description 对话客户端测试用例.<br/>
 * Created at 2023-06-07 17:10
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class ApplicationClientTest {
    /**
     * 官方模型调用应用、自训练模型应用示例
     */
    @Test
    public void testModelCompletions() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatSystemMessage("你是一名历史学家, 帮助回答各种历史问题和历史知识"));
        messages.add(new ChatUserMessage("帮我生成一篇200字的文章，描述一下春秋战国的文化和经济"));

        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setMessages(messages)
                .setParameters(new CompletionsRequest.Parameter().setResultFormat("message"));

        CompletionsResponse response = client.completions(request);

        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, content: %s, ", response.getRequestId(), response.getData().getChoices().get(0).getMessage().getContent());
    }

    /**
     * 官方大模型调用应用、自训练模型应用-其他参数使用示例
     */
    @Test
    public void testCompletionsWithParams() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .connectOptions(new ConnectOptions(30000, 60000, 60000))
                .build();

        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatSystemMessage("你是一个旅行专家, 能够帮我们制定旅行计划"));
        messages.add(new ChatUserMessage("我想去北京"));
        messages.add(new ChatAssistantMessage("北京是一个非常值得去的地方"));
        messages.add(new ChatUserMessage("那边有什么推荐的旅游景点"));

        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                //设置模型参数topP的值
                .setTopP(0.3)
                //开启历史上下文, sessionId需要采用uuid保证唯一性, 后续传入相同sessionId，百炼平台将自动维护历史上下文
                .setSessionId(UUIDGenerator.generate())
                //设置历史上下文, 由调用侧维护历史上下文, 如果同时传入sessionId和history, 优先使用调用者管理的对话上下文
                .setMessages(messages)
                .setParameters(new CompletionsRequest.Parameter()
                        //设置模型参数topK
                        .setTopK(50)
                        //设置模型参数seed
                        .setSeed(2222)
                        //设置模型参数temperature
                        .setTemperature(0.7)
                        //设置最大内容token数
                        .setMaxTokens(50)
                        //设置停止词
                        .setStop(Collections.singletonList("景点"))
                        //设置内容返回结构为message
                        .setResultFormat("message"));

        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, content: %s。", response.getRequestId(), response.getData().getChoices().get(0).getMessage().getContent());
        if (response.getData().getUsage() != null && response.getData().getUsage().size() > 0) {
            CompletionsResponse.Usage usage = response.getData().getUsage().get(0);
            System.out.printf("model: %s, input tokens: %d, output tokens: %d\n", usage.getModelId(), usage.getInputTokens(), usage.getOutputTokens());
        }
    }

    /**
     * 流式响应使用示例
     */
    @Test
    public void testCompletionsWithStream() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatSystemMessage("你是一名天文学家, 能够帮助小学生回答宇宙与天文方面的问题"));
        messages.add(new ChatUserMessage("宇宙中为什么会存在黑洞"));

        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setMessages(messages)
                .setParameters(new CompletionsRequest.Parameter()
                        //开启增量输出模式，后面输出不会包含已经输出的内容
                        .setIncrementalOutput(true)
                        //返回choice message结果
                        .setResultFormat("message")
                );

        CountDownLatch latch = new CountDownLatch(1);
        Flux<CompletionsResponse> response = client.streamCompletions(request);

        response.subscribe(
                data -> {
                    if (data.isSuccess()) {
                        System.out.printf("%s", data.getData().getChoices().get(0).getMessage().getContent());
                    } else {
                        System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                                data.getRequestId(), data.getCode(), data.getMessage());
                    }
                },
                err -> {
                    System.out.printf("failed to create completion, err: %s\n", ExceptionUtils.getStackTrace(err));
                    latch.countDown();
                },
                () -> {
                    System.out.println("\ncreate completion completely");
                    latch.countDown();
                }
        );

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BaiLianSdkException(e);
        }
    }

    /**
     * 三方模型应用示例
     */
    @Test
    public void testThirdModelCompletions() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<CompletionsRequest.ChatQaPair> history = new ArrayList<>();
        history.add(new CompletionsRequest.ChatQaPair("我想去北京", "北京是一个非常值得去的地方"));

        String prompt = "那边有什么推荐的旅游景点";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setHistory(history);

        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s", response.getRequestId(), response.getData().getText());
    }

    /**
     * 检索增强应用示例
     */
    @Test
    public void testRagAppCompletions() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<CompletionsRequest.ChatQaPair> history = new ArrayList<>();
        history.add(new CompletionsRequest.ChatQaPair("API接口如何使用", "API接口需要传入prompt、app id并通过post方法调用"));

        String prompt = "API接口说明中, TopP参数改如何传递?";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setHistory(history)
                // 返回文档检索的文档引用数据, 传入为simple或indexed
                .setDocReferenceType(DocReferenceTypeEnum.SIMPLE.getType())
                // 文档标签code列表
                .setDocTagCodes(Arrays.asList("471d*******3427", "881f*****0c232"));

        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s\n", response.getRequestId(), response.getData().getText());
        List<CompletionsResponse.DocReference> docReferences = response.getData().getDocReferences();
        if (docReferences != null && docReferences.size() > 0) {
            System.out.printf("Doc ref: %s", docReferences.get(0).getDocName());
        }
    }

    /**
     * 插件和流程编排应用使用示例
     */
    @Test
    public void testFlowAppCompletions() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        String bizParams = "{\"userId\": \"123\"}";

        String prompt = "今天杭州的天气怎么样";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setBizParams(JSON.parseObject(bizParams));

        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s", response.getRequestId(), response.getData().getText());
    }

    /**
     * 智能问数应用使用示例
     */
    @Test
    public void testNl2SqlCompletion() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        String sqlSchema = "{" +
                "    sqlInput: {" +
                "      \"synonym_infos\": \"国民生产总值: GNP|Gross National Product\"," +
                "      \"schema_infos\": [" +
                "        {" +
                "          \"columns\": [" +
                "            {" +
                "              \"col_caption\": \"地区\"," +
                "              \"col_name\": \"region\"" +
                "            }," +
                "            {" +
                "              \"col_caption\": \"年份\"," +
                "              \"col_name\": \"year\"" +
                "            }," +
                "            {" +
                "              \"col_caption\": \"国民生产总值\"," +
                "              \"col_name\": \"gross_national_product\"" +
                "            }" +
                "          ]," +
                "          \"table_id\": \"t_gross_national_product_1\"," +
                "          \"table_desc\": \"国民生产总值表\"" +
                "        }" +
                "      ]" +
                "    }" +
                "  }";

        String prompt = "浙江近五年GNP总和是多少";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setBizParams(JSON.parseObject(sqlSchema));

        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s\n", response.getRequestId(), response.getData().getText());
    }
}
