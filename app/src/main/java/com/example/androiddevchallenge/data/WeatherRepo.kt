/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import kotlin.random.Random

object WeatherRepo {
    internal val FOR_TESTS = getWeather(
        today = LocalDate.of(2020, Month.MARCH, 24),
        days = 10,
        random = Random(1234),
    )

    fun getWeather(
        today: LocalDate,
        days: Int = 10,
        random: Random = Random.Default,
    ): List<DayWeather> {
        return (0 until days).map { dayOffset ->
            val date = today.plusDays(dayOffset.toLong())
            val dateTimeBase = date.atTime(6, 0)
            val hourly = (0 until 24).map { hourOffset ->
                HourWeather(
                    hour = dateTimeBase.plusHours(hourOffset.toLong()),
                    temp = random.nextTemperature(),
                    conditions = random.nextConditions()
                )
            }
            val low: Temperature = hourly.minOf(HourWeather::temp)
            val high: Temperature = hourly.maxOf(HourWeather::temp)
            val conditions = hourly.map(HourWeather::conditions).reduce(Conditions::times)
            DayWeather(
                date = date,
                high = high,
                low = low,
                conditions = conditions,
                hourly = hourly,
            )
        }
    }
}

data class DayWeather(
    val date: LocalDate,
    val high: Temperature,
    val low: Temperature,
    val conditions: Conditions,
    val hourly: List<HourWeather>,
)

data class HourWeather(
    val hour: LocalDateTime,
    val temp: Temperature,
    val conditions: Conditions,
)

data class Temperature(
    val degreesC: Float,
) : Comparable<Temperature> {
    override operator fun compareTo(other: Temperature): Int {
        return degreesC.compareTo(other.degreesC)
    }

    val degreesF: Float get() = (degreesC * 1.8f) - 32f
}

fun Random.nextTemperature(): Temperature {
    return Temperature(degreesC = nextFloat() * 40f)
}

data class Conditions(
    val cloudy: Float,
    val rainy: Float,
    val snowy: Float,
    val lightning: Float,
) {
    operator fun times(b: Conditions): Conditions {
        return Conditions(
            cloudy = cloudy * b.cloudy,
            rainy = rainy * b.rainy,
            snowy = snowy * b.snowy,
            lightning = lightning * b.lightning,
        )
    }
}

private fun Random.nextConditions(): Conditions {
    return Conditions(
        cloudy = nextFloat(),
        rainy = nextFloat(),
        snowy = nextFloat(),
        lightning = nextFloat(),
    )
}
