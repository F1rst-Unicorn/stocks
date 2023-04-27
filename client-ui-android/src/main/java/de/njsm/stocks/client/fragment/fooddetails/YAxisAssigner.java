/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.fragment.fooddetails;

import com.github.mikephil.charting.components.YAxis;
import de.njsm.stocks.client.business.entities.PlotByUnit;
import de.njsm.stocks.client.business.entities.PlotPoint;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

class YAxisAssigner {

    private static final double MINIMUM_DISTANCE_TO_HAVE_TWO_AXES = 100;

    private final List<PlotByUnit<LocalDateTime>> plotByUnits;

    private final List<YAxis.AxisDependency> affinities;

    private double distanceBetweenMaxima;

    YAxisAssigner(List<PlotByUnit<LocalDateTime>> plotByUnits) {
        this.plotByUnits = plotByUnits;
        affinities = new ArrayList<>();
        compute();
    }

    private void compute() {
        List<Double> maximumYValues = plotByUnits.stream()
                .map(PlotByUnit::plotPoints)
                .map(v -> v.stream().map(PlotPoint::y).mapToDouble(BigDecimal::doubleValue)
                        .max().orElse(0))
                .collect(Collectors.toList());

        double largestMaximum = maximumYValues.stream().max(Comparator.comparingDouble(x -> x)).orElse(0.0);
        double smallestMaximum = maximumYValues.stream().min(Comparator.comparingDouble(x -> x)).orElse(0.0);
        distanceBetweenMaxima = abs(largestMaximum - smallestMaximum);

        for (var maximum : maximumYValues) {
            double distanceToLargest = abs(maximum - largestMaximum);
            double distanceToSmallest = abs(maximum - smallestMaximum);
            if (distanceToLargest < distanceToSmallest && distanceBetweenMaxima > MINIMUM_DISTANCE_TO_HAVE_TWO_AXES) {
                affinities.add(YAxis.AxisDependency.RIGHT);
            } else {
                affinities.add(YAxis.AxisDependency.LEFT);
            }
        }
    }

    boolean needsTwoAxes() {
        return distanceBetweenMaxima > MINIMUM_DISTANCE_TO_HAVE_TWO_AXES;
    }

    YAxis.AxisDependency getAffinityOf(int plotIndex) {
        return affinities.get(plotIndex);
    }

    String axisHintForLegend(int plotIndex) {
        return needsTwoAxes() ?
                (getAffinityOf(plotIndex) == YAxis.AxisDependency.LEFT ? " (<-)" : " (->)")
                : "";
    }
}
