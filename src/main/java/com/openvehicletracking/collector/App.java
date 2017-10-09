package com.openvehicletracking.collector;

import com.openvehicletracking.collector.codec.AlarmCodec;
import com.openvehicletracking.collector.codec.QueryCodec;
import com.openvehicletracking.collector.codec.RecordCodec;
import com.openvehicletracking.collector.codec.UpdateResultCodec;
import com.openvehicletracking.collector.db.Query;
import com.openvehicletracking.collector.db.Record;
import com.openvehicletracking.collector.db.UpdateResult;
import com.openvehicletracking.core.DeviceRegistry;
import com.openvehicletracking.core.alarm.Alarm;
import com.openvehicletracking.device.xtakip.XTakip;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by oksuz on 19/05/2017.
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        String configFile = System.getProperty("conf");
        if (configFile == null) {
            throw new RuntimeException("please specify your config file like -Dconf=/path/to/config.json");
        }

        LOGGER.info("Starting app with config file: {}", configFile);

        String config;
        try {
            config = new Scanner(new File(configFile), "UTF-8").useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        JsonObject jsonConf = new JsonObject(config);
        LOGGER.debug("Config: {}", jsonConf.encodePrettily());


        DeviceRegistry.getInstance().register(new XTakip());
        ClusterManager clusterManager = new HazelcastClusterManager();

        VertxOptions vertxOptions = new VertxOptions()
                .setClustered(true)
                .setClusterManager(clusterManager)
                .setEventLoopPoolSize(2)
                .setHAEnabled(true)
                .setWorkerPoolSize(4)
                .setHAGroup("openvehicletracking");

        new VerticleDeployer(vertxOptions, verticleDeployer -> {
            verticleDeployer.registerEventBusCodec(Record.class, new RecordCodec());
            verticleDeployer.registerEventBusCodec(Query.class, new QueryCodec());
            verticleDeployer.registerEventBusCodec(Alarm.class, new AlarmCodec());
            verticleDeployer.registerEventBusCodec(UpdateResult.class, new UpdateResultCodec());

            JsonArray verticles = jsonConf.getJsonArray("verticles");
            verticles.forEach(v -> {
                JsonObject verticleConfig = (JsonObject)v;
                try {
                    Class<?> verticleClass = Class.forName(verticleConfig.getString("id"));
                    DeploymentOptions deploymentOptions = new DeploymentOptions(verticleConfig.getJsonObject("options"));
                    deploymentOptions.setConfig(jsonConf);
                    verticleDeployer.deployVerticle(verticleClass, deploymentOptions);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("class not found", e);
                }
            });

        });



    }

}
