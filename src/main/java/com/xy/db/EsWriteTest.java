package com.xy.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试es写入数据的性能
 * 个线程创建一个对象的实例，然后顺序执行 setupTest(),runTest() Created
 * by yuwb
 */
public class EsWriteTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(EsWriteTest.class);

    private String esHost;
    private int esPort;
    private int batchSize;
    private String fromIndex;
    private String toIndex;
    private BulkRequest bulkRequest;

    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("esHost", "localhost");
        arguments.addArgument("esPort", "9200");
        arguments.addArgument("batchSize", "1000");
        arguments.addArgument("fromIndex", "cat-service-provider-v2");
        arguments.addArgument("toIndex", "test");
        return arguments;
    }

    /**
     * 每个线程初始化一遍
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        esHost = context.getParameter("esHost");
        esPort = context.getIntParameter("esPort");
        batchSize = context.getIntParameter("batchSize");
        fromIndex = context.getParameter("fromIndex");
        toIndex = context.getParameter("toIndex");
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(esHost, esPort, "http")));) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(batchSize);
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(fromIndex);
            searchRequest.types("doc");
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Map<String, Object>> docListGroup = new ArrayList<>(batchSize);
            for (SearchHit searchHit : searchResponse.getHits()) {
                Map<String, Object> docMap = new HashMap<>(19);
                docMap.putAll(searchHit.getSourceAsMap());
                docMap.remove("data");
                docMap.remove("parentMessageId");
                docMap.remove("rootMessageId");
                docMap.remove("threadGroupName");
                docMap.remove("threadId");
                docMap.remove("threadName");
                docListGroup.add(docMap);
            }
            BulkRequest bulkRequest = new BulkRequest();
            for (Map<String, Object> doc : docListGroup) {
                IndexRequest indexRequest = new IndexRequest();
                indexRequest.source(doc);
                indexRequest.index(toIndex);
                indexRequest.type("doc");
                bulkRequest.add(indexRequest);
            }
            this.bulkRequest = bulkRequest;
        } catch (Exception e) {
            log.error("load TestData", e);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(esHost, esPort, "http")));) {
            BulkResponse bulkResponse = client.bulk(this.bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                result.setSuccessful(false);
                result.setResponseData(bulkResponse.buildFailureMessage(), "utf-8");
            } else {
                result.setSuccessful(true);
                result.setResponseData(bulkResponse.status().toString(), "utf-8");
            }

        } catch (Exception e) {
            result.setSuccessful(false);
            log.error(e);
        } finally {
            result.sampleEnd();
        }
        return result;
    }
}
