package edu.generalpuzzle;

import edu.generalpuzzle.resources.SolutionsResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class PuzzlesApplication extends Application<PuzzlesConfiguration> {

    public static void main(final String[] args) throws Exception {
        new PuzzlesApplication().run(args);
    }

    @Override
    public String getName() {
        return "Puzzles";
    }

    @Override
    public void initialize(final Bootstrap<PuzzlesConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final PuzzlesConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        SolutionsResource resource = new SolutionsResource();
        environment.jersey().register(resource);
    }

}
