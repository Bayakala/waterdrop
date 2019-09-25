package io.github.interestinglab.waterdrop.flink.transform;

import com.typesafe.config.waterdrop.Config;
import io.github.interestinglab.waterdrop.common.config.CheckConfigUtil;
import io.github.interestinglab.waterdrop.flink.FlinkEnvironment;
import io.github.interestinglab.waterdrop.flink.batch.FlinkBatchTransform;
import io.github.interestinglab.waterdrop.flink.stream.FlinkStreamTransform;
import io.github.interestinglab.waterdrop.flink.util.TableUtil;
import io.github.interestinglab.waterdrop.common.config.CheckResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * @author mr_xiong
 * @date 2019-07-12 18:55
 * @description
 */
public class TableToDataStream implements FlinkStreamTransform<Void, Row>, FlinkBatchTransform<Void, Row> {


    private Config config;

    private boolean isAppend;

    @Override
    public DataStream<Row> processStream(FlinkEnvironment env, DataStream<Void> dataStream) {
        StreamTableEnvironment tableEnvironment = env.getStreamTableEnvironment();
        Table table = tableEnvironment.scan(SOURCE_TABLE_NAME);
        return TableUtil.tableToDataStream(tableEnvironment, table, isAppend);
    }

    @Override
    public DataSet<Row> processBatch(FlinkEnvironment env, DataSet<Void> data) {

        BatchTableEnvironment batchTableEnvironment = env.getBatchTableEnvironment();
        Table table = batchTableEnvironment.scan(SOURCE_TABLE_NAME);
        return TableUtil.tableToDataSet(batchTableEnvironment, table);
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public Config getConfig() {
        return config;
    }


    @Override
    public CheckResult checkConfig() {
        return CheckConfigUtil.check(config, SOURCE_TABLE_NAME);
    }

    @Override
    public void prepare() {
        if (config.hasPath("is_append")) {
            isAppend = config.getBoolean("is_append");
        }
    }
}
