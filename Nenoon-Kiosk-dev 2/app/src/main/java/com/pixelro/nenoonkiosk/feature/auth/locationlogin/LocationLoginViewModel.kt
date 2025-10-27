package com.pixelro.nenoonkiosk.feature.auth.locationlogin

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class LocationLoginViewModel @Inject constructor(
    private val application: Application,
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<LocationLoginState, LocationLoginSideEffect> {

    override val container: Container<LocationLoginState, LocationLoginSideEffect> =
        container(LocationLoginState())

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    fun checkLocationPermission() = intent {
        val hasPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        reduce {
            state.copy(isLocationPermissionGranted = hasPermission)
        }

        if (!hasPermission) {
            postSideEffect(LocationLoginSideEffect.RequestLocationPermission)
        }
    }

    fun checkLocationEnabled() = intent {
        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        reduce {
            state.copy(isLocationEnabled = isEnabled)
        }

        if (!isEnabled) {
            postSideEffect(LocationLoginSideEffect.RequestEnableLocation)
        }
    }

    fun updatePermissionGranted(granted: Boolean) = intent {
        reduce {
            state.copy(isLocationPermissionGranted = granted)
        }
    }

    fun signInWithLocation() = intent {
        if (!state.isLocationPermissionGranted) {
            reduce {
                state.copy(locationStatus = StringProvider.getString(R.string.location_signin_permission_required))
            }
            postSideEffect(LocationLoginSideEffect.RequestLocationPermission)
            return@intent
        }

        if (!state.isLocationEnabled) {
            reduce {
                state.copy(locationStatus = StringProvider.getString(R.string.location_signin_location_disabled))
            }
            postSideEffect(LocationLoginSideEffect.RequestEnableLocation)
            return@intent
        }

        reduce {
            state.copy(
                isCheckingLocation = true,
                locationStatus = StringProvider.getString(R.string.location_signin_fetching_location)
            )
        }

        try {
            val location = getCurrentLocation()

            if (location != null) {
                val latitude = location.first
                val longitude = location.second

                reduce {
                    state.copy(
                        currentLatitude = latitude,
                        currentLongitude = longitude,
                        locationStatus = StringProvider.getString(R.string.location_signin_verifying_location)
                    )
                }

                val isWithinRange = isLocationWithinRange(latitude, longitude)

                if (isWithinRange) {
                    reduce {
                        state.copy(
                            locationStatus = StringProvider.getString(R.string.location_signin_success),
                            isCheckingLocation = false
                        )
                    }
                    postSideEffect(LocationLoginSideEffect.LoginSuccess)
                } else {
                    reduce {
                        state.copy(
                            locationStatus = StringProvider.getString(R.string.location_signin_out_of_range),
                            isCheckingLocation = false
                        )
                    }
                    postSideEffect(LocationLoginSideEffect.LoginFailed)
                }
            } else {
                reduce {
                    state.copy(
                        locationStatus = StringProvider.getString(R.string.location_signin_failed),
                        isCheckingLocation = false
                    )
                }
                postSideEffect(LocationLoginSideEffect.LoginFailed)
            }
        } catch (e: Exception) {
            Log.e("LocationLoginVM", "Location sign in error: ${e.message}", e)
            reduce {
                state.copy(
                    locationStatus = StringProvider.getString(R.string.location_signin_error),
                    isCheckingLocation = false
                )
            }
            postSideEffect(LocationLoginSideEffect.LoginFailed)
        }
    }

    private suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val cancellationTokenSource = CancellationTokenSource()
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()

                location?.let { Pair(it.latitude, it.longitude) }
            } catch (e: Exception) {
                Log.e("LocationLoginVM", "Error getting current location: ${e.message}", e)
                null
            }
        }
    }

    private fun isLocationWithinRange(latitude: Double, longitude: Double): Boolean {
        val targetLat = AppConstants.LOCATION_SIGNIN_TARGET_LATITUDE
        val targetLon = AppConstants.LOCATION_SIGNIN_TARGET_LONGITUDE
        val maxDistance = AppConstants.LOCATION_SIGNIN_MAX_DISTANCE_METERS

        val distance = calculateDistance(latitude, longitude, targetLat, targetLon)
        Log.d("LocationLoginVM", "Distance from target: $distance meters")

        return distance <= maxDistance
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusMeters = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusMeters * c
    }

    fun navigateBack() = intent {
        postSideEffect(LocationLoginSideEffect.NavigateBack)
    }
}
