package playground.mas.dispatcher;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import playground.mas.MASConfigGroup;
import playground.mas.routing.MASCordonTravelDisutility;
import playground.sebhoerl.avtaxi.config.AVDispatcherConfig;
import playground.sebhoerl.avtaxi.dispatcher.AVDispatcher;
import playground.sebhoerl.avtaxi.dispatcher.multi_od_heuristic.AggregateRideAppender;
import playground.sebhoerl.avtaxi.dispatcher.multi_od_heuristic.MultiODHeuristic;
import playground.sebhoerl.avtaxi.dispatcher.multi_od_heuristic.TravelTimeEstimator;
import playground.sebhoerl.avtaxi.framework.AVModule;

public class MASPoolDispatcherFactory implements AVDispatcher.AVDispatcherFactory {
    @Inject private Network network;
    @Inject private EventsManager eventsManager;

    @Inject @Named(AVModule.AV_MODE)
    private TravelTime travelTime;

    @Inject private MASConfigGroup masConfig;
    @Inject private MASCordonTravelDisutility cordonDisutility;

    @Override
    public AVDispatcher createDispatcher(AVDispatcherConfig config) {
        MASRouterFactory factory = new MASRouterFactory(network, travelTime, cordonDisutility, masConfig.getChargedOperatorIds().contains(config.getParent().getId()));
        LeastCostPathCalculator router = factory.createRouter();

        double threshold = Double.parseDouble(config.getParams().getOrDefault("aggregationThreshold", "600.0"));
        TravelTimeEstimator estimator = new TravelTimeEstimator(router, threshold);

        return new MultiODHeuristic(
                config.getParent().getId(),
                eventsManager,
                network,
                new AggregateRideAppender(config, router, travelTime, estimator),
                estimator
        );
    }
}
