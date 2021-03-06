package org.styloot.hobo.thriftserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.thrift.TException;
import org.styloot.hobo.gen.*;
import org.styloot.hobo.*;
import org.styloot.hobo.hoboindex.HoboIndex;

import java.util.*;

class HoboServerImpl implements Hobo.Iface {
    private static final Logger log = LoggerFactory.getLogger(HoboServerImpl.class);

    public HoboServerImpl(HoboIndex idx, int ps) {
	index = idx;
	pageSize = ps;
    }
    HoboIndex index;
    int pageSize;

    public List<String> find(String category_name, List<String> features, byte red, byte green, byte blue, double colorDist, int cost_min, int cost_max, int page) throws TException {
	Vector<String> result = new Vector<String>(pageSize);
	int count = 0;
	Iterator<Item> iter;
	log.info("Running query with cost: " + cost_min + ", " + cost_max);
	if (colorDist > 0) {
	    iter = index.find(category_name, features, CIELabColor.CIELabFromRGB(red, green, blue), colorDist, cost_min, cost_max);
	} else {
	    iter = index.find(category_name, features, null, 0, cost_min, cost_max);
	}
	while (iter.hasNext()) {
	    Item item = iter.next();
	    if (count >= page*pageSize) {
		result.add(item.id);
	    }
	    if (count >= (page+1)*pageSize) {
		break;
	    }
	    count += 1;
	}
	return result;
    }
}
