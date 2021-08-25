package poc.pipelines;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.TestSources;
import poc.jdbcservice.CallParameters;
import poc.jdbcservice.Context;
import poc.jdbcservice.GetFunds;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class InvokeJDBCService {

    private final String connectionURL = "jdbc:...";
    //private final String storedProcCall = "call get_avail_funds(?)"; // account number as parameter

    private Pipeline createPipeline() {

        // Originally called .toNonCooperative on the service factory, but now that we have
        // moved to using mapUsingServiceAsync that should  no longer be necessary.
        ServiceFactory<?, Context> jdbcServiceFactory =
                ServiceFactories.sharedService((ctx) -> Context.create("user", "password", "url"));

        Pipeline p = Pipeline.create();
        p.readFrom(TestSources.itemStream(2))
                .withIngestionTimestamps()
                .map(item -> {
                    // Unnecessary stage, just for showing how to access the item's timestamp
                    System.out.println("Timestamp " + item.timestamp());
                    return item;
                })
                .mapUsingServiceAsync(jdbcServiceFactory, (service, streamitem) -> {
                    // TODO: get parameters from streamitem
                    CallParameters cp = new CallParameters("Account12345");
                    GetFunds storedProc = new GetFunds();
                    CompletableFuture<ResultSet> future = storedProc.applyEx(service, cp);
                    return future;
                })
                // TODO: update results map with data from the result set
                //  if all results have posted, may sink to ready-to-aggregate map
                //  if waiting for more results, probably sink to Sinks.noop() instead
                .writeTo(Sinks.logger());

        return p;
    }


    public static void main(String[] args) {
        JetInstance jet = Jet.newJetInstance(); // TODO: make client connection instead after testing done
        InvokeJDBCService main = new InvokeJDBCService();
        Pipeline p = main.createPipeline();
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName("demo");
        Job job = jet.newJob(p, jobConfig);
        // although we exit, the job continues to run
    }
}
