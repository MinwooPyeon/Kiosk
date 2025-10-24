package com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction

data class PulmonaryFunctionTestResult(
    var pulmonaryPower: Double = 0.0,
    var pulmonaryCapacity: Double = 0.0,
    var pulmonaryAge: Int = 0,
    var pulmonaryStatus: Boolean = false,
)
