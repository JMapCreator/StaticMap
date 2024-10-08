package com.hotcoffee.staticmap;


import com.hotcoffee.staticmap.geo.LocationBounds;
import com.hotcoffee.staticmap.layers.TMSLayer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticMapTest {

    @Test
    public void testShould_initialize_map_object() {
        // Given a simple static map object
        // When it get initialized
        StaticMap staticMap = new StaticMap(10, 10);

        // Then it should have correct parameters
        assertThat(staticMap.getHeight()).isEqualTo(10);
        assertThat(staticMap.getWidth()).isEqualTo(10);
    }

}