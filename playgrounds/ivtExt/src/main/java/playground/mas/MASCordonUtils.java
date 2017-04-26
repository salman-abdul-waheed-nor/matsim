package playground.mas;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.vehicles.Vehicle;
import playground.sebhoerl.avtaxi.data.AVOperator;

import java.util.*;
import java.util.stream.Collectors;

public class MASCordonUtils {
    static public boolean isChargeableOperator(Id<Vehicle> vehicleId, Collection<Id<AVOperator>> chargeableOperators) {
        String stringId = vehicleId.toString();

        if (stringId.startsWith("av_")) {
            for (Id<AVOperator> operatorId : chargeableOperators) {
                if (stringId.startsWith("av_" + operatorId.toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    static public Collection<Link> findInsideCordonLinks(Id<Node> centerId, double radius, Network network) {
        final Node centerNode = network.getNodes().get(centerId);

        return network.getLinks().values().stream().parallel()
                .filter(l -> CoordUtils.calcEuclideanDistance(l.getFromNode().getCoord(), centerNode.getCoord()) < radius)
                .collect(Collectors.toSet());
    }

    static public Collection<Link> findChargeableCordonLinks(Id<Node> centerId, double radius, Network network) {
        final Node centerNode = network.getNodes().get(centerId);

        return network.getLinks().values().stream().parallel()
                .filter(l ->
                        CoordUtils.calcEuclideanDistance(l.getFromNode().getCoord(), centerNode.getCoord()) >= radius &&
                        CoordUtils.calcEuclideanDistance(l.getToNode().getCoord(), centerNode.getCoord()) <= radius
                )
                .collect(Collectors.toSet());
    }
}
