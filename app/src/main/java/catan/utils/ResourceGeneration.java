package catan.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import catan.enums.Odds;
import catan.enums.Resource;


public class ResourceGeneration {
    public static List<Tuple<Odds, Resource>> generateResources() {
        List<Tuple<Odds, Resource>> tiles = new ArrayList<Tuple<Odds, Resource>>();
        List<Odds> odds = new ArrayList<Odds>(Arrays.asList(Odds.standardOdds));
        List<Resource> resources = new ArrayList<Resource>(Arrays.asList(Resource.standardResources));

        Collections.shuffle(odds);
        Collections.shuffle(resources);

        for(int i = 0; i < 18; i++) {
            tiles.add(new Tuple<Odds, Resource>(odds.get(i), resources.get(i)));
        }
        tiles.add(new Tuple<Odds, Resource>(Odds.SEVEN, Resource.NONE));

        Collections.shuffle(tiles);

        return tiles;
    }
}
