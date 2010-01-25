/* *********************************************************************** *
 * project: org.matsim.*
 * OTFLanesConnectionManagerFactory
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.lanes.otfvis;

import org.matsim.lanes.otfvis.drawer.OTFLaneSignalDrawer;
import org.matsim.lanes.otfvis.io.OTFLaneReader;
import org.matsim.lanes.otfvis.io.OTFLaneWriter;
import org.matsim.lanes.otfvis.layer.OTFLaneLayer;
import org.matsim.ptproject.qsim.QLink;
import org.matsim.vis.otfvis.data.OTFConnectionManager;
import org.matsim.vis.otfvis.data.OTFConnectionManagerFactory;


/**
 * @author dgrether
 *
 */
public class OTFLanesConnectionManagerFactory implements OTFConnectionManagerFactory {

  private OTFConnectionManagerFactory delegate;

  public OTFLanesConnectionManagerFactory(OTFConnectionManagerFactory delegate) {
    this.delegate = delegate;
  }
  
  @Override
  public OTFConnectionManager createConnectionManager() {
    OTFConnectionManager connect = this.delegate.createConnectionManager();
    // data source to writer
    connect.add(QLink.class, OTFLaneWriter.class);
    // writer -> reader: from server to client
    connect
    .add(OTFLaneWriter.class, OTFLaneReader.class);
    // reader to drawer (or provider to receiver)
    connect.add(OTFLaneReader.class, OTFLaneSignalDrawer.class);
    // drawer -> layer
    connect.add(OTFLaneSignalDrawer.class, OTFLaneLayer.class);
    return connect;
  }

}
