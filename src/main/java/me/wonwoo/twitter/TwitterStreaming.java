package me.wonwoo.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wonwoo.twitter.TwitterAuthorization.getAuthorization;

/**
 * Created by wonwoo on 2016. 2. 25..
 *
 * Twitter authorization
 * TwitterAuthorization.class set token
 *
 */
public class TwitterStreaming {

    static Logger logger = LoggerFactory.getLogger(TwitterStreaming.class);
    static JavaStreamingContext javaStreamingContext;
    static ObjectMapper mapper = new ObjectMapper();

    //필터링
    static String[] filters = {"필리버스터"};

    //떨굴 파일 경로
    private static String PATH = "/Users/wonwoo/stream/twitter/";

    //10초마다
    static Integer duration = 10;

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setAppName("TwitterStreaming")
                .setMaster("local[*]");

        javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(duration));

        //스트리밍셋팅
        JavaDStream<String> stream = TwitterUtils.createStream(javaStreamingContext, getAuthorization(),filters)
                .map(tweet -> mapper.writeValueAsString(tweet));

        //print 할 갯수
        stream.print(5);

        stream.repartition(1).dstream().saveAsTextFiles(PATH, "stream");

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}
