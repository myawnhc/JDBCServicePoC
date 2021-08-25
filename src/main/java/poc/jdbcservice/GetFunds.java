package poc.jdbcservice;

import com.hazelcast.function.BiFunctionEx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class GetFunds implements BiFunctionEx<Context, CallParameters, CompletableFuture<ResultSet>> {

    //@Override
    @Deprecated
    public ResultSet applyBlockingEx(Context context, CallParameters parameters) throws Exception {
        try (
                var connection = context.getConnection();
                var statement = connection.prepareCall("{CALL GET_AVAIL_FUNDS(?)}")
        ) {
            statement.setString(1, parameters.getAccountNumber());
            ResultSet result = statement.executeQuery();
            return result;
        }
    }

    // Experimental. Likely would be nicer to unpack the ResultSet and return a ready-to-use data type.
    @Override
    public CompletableFuture<ResultSet> applyEx(Context context, CallParameters parameters) throws Exception {
        CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(() -> {
            try (
                    var connection = context.getConnection();
                    var statement = connection.prepareCall("{CALL GET_AVAIL_FUNDS(?)}")
            ) {
                statement.setString(1, parameters.getAccountNumber());
                ResultSet result = statement.executeQuery();
                return result;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
        return future;
    }
}
