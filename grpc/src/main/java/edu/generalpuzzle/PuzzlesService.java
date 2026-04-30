package edu.generalpuzzle;

import edu.generalpuzzle.core.Puzzle2D;
import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class PuzzlesService extends edu.generalpuzzle.proto.SolutionServiceGrpc.SolutionServiceImplBase {

    private static final Log log = LogFactory.getLog(PuzzlesService.class);

    /**
     * `grpcurl -d "{\"id\":\"12_5\"}" -plaintext localhost:9090 SolutionService.Solve`
     *
     * OR: `grpcui -plaintext localhost:9090`
     *
     */

    @Override
    public void solve(edu.generalpuzzle.proto.IdRequest req, StreamObserver<edu.generalpuzzle.proto.Solution> responseObserver) {
        log.info("Starting id " + req.getId());

        String[] rowsCols = req.getId().split("_");
        Puzzle2D puzzle2d = new Puzzle2D(Integer.parseInt(rowsCols[0]), Integer.parseInt(rowsCols[1]));
        int res = puzzle2d.solve();

        log.info("Done. result " + res);
        edu.generalpuzzle.proto.Solution reply = edu.generalpuzzle.proto.Solution.newBuilder().setId(req.getId()).setRes(res).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
