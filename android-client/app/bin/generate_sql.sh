#!/bin/bash
# shellcheck disable=SC2034

OUTPUT_FILE=$1
mkdir -p "$(dirname $OUTPUT_FILE)"

tables=(user user_device food fooditem eannumber location unit scaled_unit recipe recipe_product recipe_ingredient)

common_fields=(_id version valid_time_start valid_time_end transaction_time_start transaction_time_end initiates)

user_fields=( "${common_fields[@]}" name)
user_device_fields=( "${common_fields[@]}" name belongs_to)
food_fields=( "${common_fields[@]}" name to_buy expiration_offset location description store_unit)
fooditem_fields=( "${common_fields[@]}" eat_by of_type stored_in registers buys unit)
eannumber_fields=( "${common_fields[@]}" number identifies)
location_fields=( "${common_fields[@]}" name description)
recipe_fields=( "${common_fields[@]}" name instructions duration)
recipe_product_fields=( "${common_fields[@]}" amount product recipe unit)
recipe_ingredient_fields=( "${common_fields[@]}" amount ingredient recipe unit)
unit_fields=( "${common_fields[@]}" name abbreviation)
scaled_unit_fields=( "${common_fields[@]}" scale unit)

user_join_fooditem=(buys)
user_device_join_fooditem=(registers)
food_join_fooditem=(of_type)
fooditem_join_fooditem=()
eannumber_join_fooditem=()
location_join_fooditem=(stored_in)
recipe_join_fooditem=()
recipe_product_join_fooditem=()
recipe_ingredient_join_fooditem=()
unit_join_fooditem=()
scaled_unit_join_fooditem=(unit)
unit_join_scaled_unit=(unit)

cat <<EOF >"${OUTPUT_FILE}"
/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.db.entities;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;

public interface Sql {

EOF

for table in "${tables[@]}" ; do

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_TABLE = "$table";

    String ${table^^}_PREFIX = "${table}_";

    String ${table^^}_JOIN_INITIATOR =
            "join user_device initiator_device on main_table_1.initiates = initiator_device._id " +
            "join user initiator_user on initiator_user._id = initiator_device.belongs_to ";

    String ${table^^}_TIME_COLUMNS =
            "main_table_1.transaction_time_start = (select min(transaction_time_start) from ${table} x where x._id = main_table_1._id) as is_first ";

    String ${table^^}_WHERE_VALID =
            "where not (main_table_1.version != (select min(version) from ${table} x where x._id = main_table_1._id) and main_table_2._id is null and main_table_1.transaction_time_end != :infinity) " +
            "and (not (main_table_1.valid_time_end = :infinity and main_table_1.transaction_time_end = main_table_1.valid_time_end) or main_table_1.version = (select min(version) from ${table} x where x._id = main_table_1._id))";

EOF
done

for table in "${tables[@]}" ; do

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS =
EOF

    arrayname="${table}_fields"
    array="${arrayname}[@]"
    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            ${table^^}_TABLE + ".${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS_QUALIFIED =
EOF

    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            ${table^^}_TABLE + ".${field} as " + ${table^^}_PREFIX + "${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS_VERSION_1 =
EOF

    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            ${table^^}_TABLE + "_1.${field} as version1_" + ${table^^}_PREFIX + "${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS_VERSION_2 =
EOF

    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            ${table^^}_TABLE + "_2.${field} as version2_" + ${table^^}_PREFIX + "${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS_VERSION_1_PLAIN =
EOF

    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            "main_table_1.${field} as version1_${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    cat <<EOF >>"${OUTPUT_FILE}"
    String ${table^^}_FIELDS_VERSION_2_PLAIN =
EOF

    for field in "${!array}" ; do
        cat <<EOF >>"${OUTPUT_FILE}"
            "main_table_2.${field} as version2_${field}, " +
EOF
    done
    echo "            \"\";" >>"${OUTPUT_FILE}"
    echo >>"${OUTPUT_FILE}"

    for primary_table in "${tables[@]}" ; do

        arrayname="${primary_table}_join_${table}"
        array="${arrayname}[@]"
        for field in "${!array}" ; do

            cat <<EOF >>"${OUTPUT_FILE}"
    String ${primary_table^^}_JOIN_${table^^} =
            "inner join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + " " +
            "on " + ${primary_table^^}_TABLE + "._id = " + ${table^^}_TABLE + ".${field} " +
            "and " + ${primary_table^^}_TABLE + ".valid_time_start <= " + NOW +
            "and " + NOW + " < " + ${primary_table^^}_TABLE + ".valid_time_end " +
            "and " + ${primary_table^^}_TABLE + ".transaction_time_end = :infinity ";

    String ${table^^}_JOIN_${primary_table^^} =
            "inner join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + " " +
            "on " + ${table^^}_TABLE + ".${field} = " + ${primary_table^^}_TABLE + "._id " +
            "and " + ${table^^}_TABLE + ".valid_time_start <= " + NOW +
            "and " + NOW + " < " + ${table^^}_TABLE + ".valid_time_end " +
            "and " + ${table^^}_TABLE + ".transaction_time_end = :infinity ";

    String ${primary_table^^}_JOIN_${table^^}_AT_TRANSACTION_TIME =
            "inner join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + " " +
            "on " + ${primary_table^^}_TABLE + "._id = " + ${table^^}_TABLE + ".${field} " +
            "and " + ${primary_table^^}_TABLE + ".valid_time_start <= " + NOW +
            "and " + NOW + " < " + ${primary_table^^}_TABLE + ".valid_time_end " +
            "and " + ${primary_table^^}_TABLE + ".transaction_time_start <= :transactionTime " +
            "and :transactionTime < " + ${primary_table^^}_TABLE + ".transaction_time_end ";

    String ${table^^}_JOIN_${primary_table^^}_AT_TRANSACTION_TIME =
            "inner join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + " " +
            "on " + ${table^^}_TABLE + ".${field} = " + ${primary_table^^}_TABLE + "._id " +
            "and " + ${table^^}_TABLE + ".valid_time_start <= " + NOW +
            "and " + NOW + " < " + ${table^^}_TABLE + ".valid_time_end " +
            "and " + ${table^^}_TABLE + ".transaction_time_start <= :transactionTime " +
            "and :transactionTime < " + ${table^^}_TABLE + ".transaction_time_end ";

    String ${table^^}_EVENT_1_JOIN_${primary_table^^} =
            "join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + "_1 " +
            "on " + ${primary_table^^}_TABLE + "_1._id = " + ${table^^}_TABLE + "_1.${field} " +
            "and " + ${primary_table^^}_TABLE + "_1.valid_time_start <= main_table_1.transaction_time_start " +
            "and main_table_1.transaction_time_start < " + ${primary_table^^}_TABLE + "_1.valid_time_end " +
            "and " + ${primary_table^^}_TABLE + "_1.transaction_time_end = :infinity ";

    String ${primary_table^^}_EVENT_1_JOIN_${table^^} =
            "join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + "_1 " +
            "on " + ${table^^}_TABLE + "_1.${field} = " + ${primary_table^^}_TABLE + "_1._id " +
            "and " + ${table^^}_TABLE + "_1.valid_time_start <= main_table_1.transaction_time_start " +
            "and main_table_1.transaction_time_start < " + ${table^^}_TABLE + "_1.valid_time_end " +
            "and " + ${table^^}_TABLE + "_1.transaction_time_end = :infinity ";

    String ${table^^}_EVENT_2_JOIN_${primary_table^^} =
            "left outer join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + "_2 " +
            "on " + ${primary_table^^}_TABLE + "_2._id = " + ${table^^}_TABLE + "_2.${field} " +
            "and " + ${primary_table^^}_TABLE + "_2.valid_time_start <= main_table_2.transaction_time_start " +
            "and main_table_2.transaction_time_start < " + ${primary_table^^}_TABLE + "_2.valid_time_end " +
            "and " + ${primary_table^^}_TABLE + "_2.transaction_time_end = :infinity ";

    String ${primary_table^^}_EVENT_2_JOIN_${table^^} =
            "left outer join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + "_2 " +
            "on " + ${table^^}_TABLE + "_2.${field} = " + ${primary_table^^}_TABLE + "_2._id " +
            "and " + ${table^^}_TABLE + "_2.valid_time_start <= main_table_2.transaction_time_start " +
            "and main_table_2.transaction_time_start < " + ${table^^}_TABLE + "_2.valid_time_end " +
            "and " + ${table^^}_TABLE + "_2.transaction_time_end = :infinity ";

    String ${table^^}_EVENT_1_JOIN_${primary_table^^}_MAIN =
            "join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + "_1 " +
            "on " + ${primary_table^^}_TABLE + "_1._id = main_table_1.${field} " +
            "and " + ${primary_table^^}_TABLE + "_1.valid_time_start <= main_table_1.transaction_time_start " +
            "and main_table_1.transaction_time_start < " + ${primary_table^^}_TABLE + "_1.valid_time_end " +
            "and " + ${primary_table^^}_TABLE + "_1.transaction_time_end = :infinity ";

    String ${primary_table^^}_EVENT_1_JOIN_${table^^}_MAIN =
            "join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + "_1 " +
            "on main_table_1.${field} = " + ${primary_table^^}_TABLE + "_1._id " +
            "and " + ${table^^}_TABLE + "_1.valid_time_start <= main_table_1.transaction_time_start " +
            "and main_table_1.transaction_time_start < " + ${table^^}_TABLE + "_1.valid_time_end " +
            "and " + ${table^^}_TABLE + "_1.transaction_time_end = :infinity ";

    String ${table^^}_EVENT_2_JOIN_${primary_table^^}_MAIN =
            "left outer join " + ${primary_table^^}_TABLE + " " + ${primary_table^^}_TABLE + "_2 " +
            "on " + ${primary_table^^}_TABLE + "_2._id = main_table_2.${field} " +
            "and " + ${primary_table^^}_TABLE + "_2.valid_time_start <= main_table_2.transaction_time_start " +
            "and main_table_2.transaction_time_start < " + ${primary_table^^}_TABLE + "_2.valid_time_end " +
            "and " + ${primary_table^^}_TABLE + "_2.transaction_time_end = :infinity ";

    String ${primary_table^^}_EVENT_2_JOIN_${table^^}_MAIN =
            "left outer join " + ${table^^}_TABLE + " " + ${table^^}_TABLE + "_2 " +
            "on main_table_2.${field} = " + ${primary_table^^}_TABLE + "_2._id " +
            "and " + ${table^^}_TABLE + "_2.valid_time_start <= main_table_2.transaction_time_start " +
            "and main_table_2.transaction_time_start < " + ${table^^}_TABLE + "_2.valid_time_end " +
            "and " + ${table^^}_TABLE + "_2.transaction_time_end = :infinity ";

EOF
        done
    done
done

echo "}"  >>"${OUTPUT_FILE}"
