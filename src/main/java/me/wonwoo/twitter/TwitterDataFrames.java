package me.wonwoo.twitter;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * Created by wonwoo on 2016. 2. 25..
 */
public class TwitterDataFrames {

    static Logger logger = LoggerFactory.getLogger(TwitterStreaming.class);

    private static String PATH = "/Users/wonwoo/stream/twitter/*";

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("TwitterDataFrames")
                .setMaster("local[*]");

        JavaSparkContext javaSparkContext = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        DataFrame dataFrame = sqlContext.read().json(PATH);
        dataFrame.show();
        long count = dataFrame.count();
        logger.info("total Count : {} ", count);
        dataFrame.select("text").show();
        dataFrame.groupBy("text").count().show();


        dataFrame.groupBy("lang").count().show();

        //filter
        DataFrame filter = dataFrame.filter(dataFrame.col("text").rlike("spring"));

        logger.info("size : {}", filter.collectAsList().size());

        List<String> filterCollect = filter.javaRDD().map(row -> "text: " + row.getAs("text")).collect();

        logger.info(
                "filterCollect : {}", filterCollect.stream().collect(joining("\n"))
        );

        //temp 테이블 생성
        dataFrame.registerTempTable("tweets");


        //sql
        DataFrame sqlFrame = sqlContext.sql("SELECT text FROM tweets where text like '%spark%'");
        logger.info("size : {}", sqlFrame.collectAsList().size());

        List<String> sqlCollect = sqlFrame.javaRDD().map(row -> "text: " + row.getString(0)).collect();

        logger.info(
                "sqlCollect : {}", sqlCollect.stream().collect(joining("\n"))
        );
    }
}
