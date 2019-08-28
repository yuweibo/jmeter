package com.xy.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.querybuilder.BuiltQuery;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试influxDB写入数据的性能
 * <p>
 * 每个线程创建一个对象的实例，然后顺序执行 setupTest(),runTest() Created
 * by yuwb
 */
public class InfluxDBWriteTest extends AbstractJavaSamplerClient {

    static Log log = LogFactory.getLog(InfluxDBWriteTest.class);

    private String url;
    private String database;
    private String retentionPolicy;
    private String fromMeasurement;
    private String toMeasurement;
    private BatchPoints batchPoints;

    @Override
    public Arguments getDefaultParameters() {
        super.getDefaultParameters();

        Arguments arguments = new Arguments();
        arguments.addArgument("url", "http://localhost:8086");
        arguments.addArgument("database", "cat_logstash");
        arguments.addArgument("retentionPolicy", "cat_data");
        arguments.addArgument("fromMeasurement", "test_data");
        arguments.addArgument("toMeasurement", "test");
        return arguments;
    }

    /**
     * 每个线程初始化一遍
     */
    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        url = context.getParameter("url");
        database = context.getParameter("database");
        fromMeasurement = context.getParameter("fromMeasurement");
        toMeasurement = context.getParameter("toMeasurement");
        retentionPolicy = context.getParameter("retentionPolicy");

        try (InfluxDB influxDB = InfluxDBFactory.connect(url);) {
            QueryResult queryResult = influxDB.query(BuiltQuery.QueryBuilder.select().all().from(database, fromMeasurement).where());
            QueryResult.Series series = queryResult.getResults().get(0).getSeries().get(0);
            builderBatchPoints(series);
        }
    }

    private void builderBatchPoints(QueryResult.Series series) {
        AtomicLong atomicLong = new AtomicLong(0);
        BatchPoints.Builder builder = BatchPoints.database(database).retentionPolicy(retentionPolicy).consistency(InfluxDB.ConsistencyLevel.ALL);
        for (List<Object> columnVals : series.getValues()) {
            Point.Builder pb = Point.measurement(toMeasurement);
            for (int i = 0; i < columnVals.size(); i++) {
                String column = series.getColumns().get(i);
                Object columnVal = columnVals.get(i);
                if ("durationInMillis".equals(column)) {
                    pb.addField(column, ((Double) columnVal).intValue());
                } else if ("timestampInMillis".equals(column)) {
                    pb.addField(column, ((Double) columnVal).longValue());
                    pb.tag(column + "_tag", String.valueOf(atomicLong.incrementAndGet()));
                } else if ("time".equals(column)) {
                    continue;
                } else if ("data".equals(column)) {
                    continue;
                } else if ("parentMessageId".equals(column)) {
                    continue;
                } else if ("rootMessageId".equals(column)) {
                    continue;
                } else if ("threadGroupName".equals(column)) {
                    continue;
                } else if ("threadId".equals(column)) {
                    continue;
                } else if ("threadName".equals(column)) {
                    continue;
                } else {
                    pb.addField(column, columnVal.toString());
                }
            }
            builder.points(pb.build());
        }
        batchPoints = builder.build();
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        try (InfluxDB influxDB = InfluxDBFactory.connect(url);) {
            influxDB.enableBatch();
            influxDB.write(batchPoints);
            result.setSuccessful(true);
        } catch (Exception e) {
            result.setSuccessful(false);
            log.error(e);
        } finally {
            result.sampleEnd();
        }
        return result;
    }
}
