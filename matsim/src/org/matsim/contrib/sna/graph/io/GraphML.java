/* *********************************************************************** *
 * project: org.matsim.*
 * GraphML.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

/**
 * 
 */
package org.matsim.contrib.sna.graph.io;

import playground.johannes.socialnetworks.graph.io.GraphMLWriter;


/**
 * Constants used by {@link AbstractGraphMLReader} and {@link GraphMLWriter}.
 * 
 * @author illenberger
 * 
 */
public interface GraphML {

	public static final String GRAPHML_TAG = "graphml";

	public static final String XMLNS_TAG = "xmlns";

	public static final String XMLNS_URL = "http://graphml.graphdrawing.org/xmlns";

	public static final String GRAPH_TAG = "graph";

	public static final String EDGEDEFAULT_TAG = "edgedefault";

	public static final String UNDIRECTED = "undirected";

	public static final String DIRECTED = "directed";

	public static final String ID_TAG = "id";

	public static final String SOURCE_TAG = "source";

	public static final String TARGET_TAG = "target";

	public static final String NODE_TAG = "node";

	public static final String EDGE_TAG = "edge";

}
