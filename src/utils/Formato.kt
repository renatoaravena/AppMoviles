package utils

import java.util.Locale

// Formato de pesos chilenos: 8990.0 -> "$8.990"
fun formatearPesos(monto: Double): String =
    "\$" + String.format(Locale.forLanguageTag("es-CL"), "%,.0f", monto)