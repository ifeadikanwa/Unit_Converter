package converter

import java.lang.Exception
import java.util.*

val scanner = Scanner(System.`in`)

//and their meter equivalent
enum class Length(val number: Double, val units: Array<String>){
    METER(1.0, arrayOf("m", "meter", "meters")),
    KILOMETER(1000.0, arrayOf("km", "kilometer", "kilometers")),
    CENTIMETER(0.01, arrayOf("cm", "centimeter", "centimeters")),
    MILLIMETER(0.001, arrayOf( "mm", "millimeter", "millimeters")),
    MILE(1609.35, arrayOf( "mi", "mile", "miles")),
    YARD(0.9144, arrayOf( "yd", "yard", "yards")),
    FOOT(0.3048, arrayOf("ft", "foot", "feet")),
    INCH(0.0254, arrayOf( "in", "inch", "inches")),
    NULL(0.0, arrayOf())
}

//and their gram equivalent
enum class Weight(val number: Double, val units: Array<String>){
    GRAM(1.0, arrayOf("g", "gram", "grams")),
    KILOGRAM(1000.0, arrayOf("kg", "kilogram", "kilograms")),
    MILLIGRAM(0.001, arrayOf("mg", "milligram", "milligrams")),
    POUNDS(453.592, arrayOf("lb", "pound", "pounds")),
    OUNCES(28.3495, arrayOf("oz", "ounce", "ounces")),
    NULL(0.0, arrayOf())
}

//and their celsius equivalent
enum class Temperature(val units: Array<String>){
    CELSIUS( arrayOf("degree Celsius", "degrees Celsius", "celsius", "dc", "c")),
    FAHRENHEIT( arrayOf("degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f")),
    KELVIN( arrayOf("kelvin", "kelvins", "k")),
    NULL( arrayOf(""))
}

fun main() {
    var go = true

    while(go){
        println("Enter what you want to convert (or exit):")
        val userInput = scanner.nextLine()

        if(userInput.equals("exit", ignoreCase = true)) go=false else println(converter(userInput) + "\n")
    }

}

fun converter(instruction: String) : String{
    val splitString = instruction.split(" ")

    try {
        splitString[0].toDouble()
    }
    catch (e: Exception){
        return "Parse error"
    }

    var amount: Double
    var fromUnit: String
    var toUnit: String


    if (splitString.size > 4){
        amount = splitString[0].toDouble()

        if (splitString[1].equals("degree", ignoreCase = true) || splitString[1].equals("degrees", ignoreCase = true)){
            fromUnit = "${splitString[1]} ${splitString[2]}"

            if (splitString[4].equals("degree", ignoreCase = true) || splitString[4].equals("degrees", ignoreCase = true)){
                toUnit = "${splitString[4]} ${splitString[5]}"
            }
            else{
                toUnit = splitString[4]
            }
        }
        else{
            fromUnit = splitString[1]
            toUnit = "${splitString[3]} ${splitString[4]}"
        }
    }
    else{
        amount = splitString[0].toDouble()
        fromUnit = splitString[1].trim()
        toUnit = splitString[3].trim()
    }


    val(fromUnitTempEnum, toUnitTempEnum) = findTempUnitEnum(fromUnit, toUnit)
    val(fromUnitLengthEnum, toUnitLengthEnum) = findLengthUnitEnum(fromUnit, toUnit)
    val(fromUnitWeightEnum, toUnitWeightEnum) = findWeightUnitEnum(fromUnit, toUnit)

    if(fromUnitTempEnum != Temperature.NULL && toUnitTempEnum != Temperature.NULL){
        val celsiusEquivalent = when(fromUnitTempEnum){
            Temperature.CELSIUS -> amount
            Temperature.FAHRENHEIT -> F_TO_C(amount)
            Temperature.KELVIN -> K_TO_C(amount)
            Temperature.NULL -> return "Error"
        }

        val toFinalUnit = when(toUnitTempEnum){
            Temperature.CELSIUS -> celsiusEquivalent
            Temperature.FAHRENHEIT -> C_TO_F(celsiusEquivalent)
            Temperature.KELVIN -> C_TO_K(celsiusEquivalent)
            Temperature.NULL -> return "Error"
        }

        return "$amount ${if(amount == 1.0) fromUnitTempEnum.units[0] else fromUnitTempEnum.units[1]} " +
                "is $toFinalUnit ${if(toFinalUnit == 1.0) toUnitTempEnum.units[0] else toUnitTempEnum.units[1]}"
    }
    else if (fromUnitTempEnum != Temperature.NULL || toUnitTempEnum != Temperature.NULL){
        if(fromUnitTempEnum != Temperature.NULL){
            return "Conversion from " +
                    "${fromUnitTempEnum.units[1]} " +
                    "to " +
                    "${if(toUnitLengthEnum != Length.NULL) toUnitLengthEnum.units[2] else if(toUnitWeightEnum != Weight.NULL)toUnitWeightEnum.units[2] else "???"} " +
                    "is impossible"
        }
        if(toUnitTempEnum != Temperature.NULL){
            return "Conversion from " +
                    "${if(fromUnitLengthEnum != Length.NULL) fromUnitLengthEnum.units[2] else if(fromUnitWeightEnum != Weight.NULL) fromUnitWeightEnum.units[2] else "???"} " +
                    "to " +
                    "${toUnitTempEnum.units[1]} " +
                    "is impossible"
        }
    }



    //both units are lengths
    if(fromUnitLengthEnum != Length.NULL && toUnitLengthEnum != Length.NULL){
        //conversion
        if(amount < 0.0) return "Length shouldn't be negative"

        val toMeter = amount * fromUnitLengthEnum.number
        val toFinalUnit = toMeter / toUnitLengthEnum.number

        return "$amount ${if(amount == 1.0) fromUnitLengthEnum.units[1] else fromUnitLengthEnum.units[2]} " +
                "is $toFinalUnit ${if(toFinalUnit == 1.0) toUnitLengthEnum.units[1] else toUnitLengthEnum.units[2]}"
    }
    else{
        //both unit are weights
        if (fromUnitWeightEnum != Weight.NULL && toUnitWeightEnum != Weight.NULL){
            //conversion
            if(amount < 0.0) return "Weight shouldn't be negative"

            val toGram = amount * fromUnitWeightEnum.number
            val toFinalUnit = toGram / toUnitWeightEnum.number

            return "$amount ${if(amount == 1.0) fromUnitWeightEnum.units[1] else fromUnitWeightEnum.units[2]} " +
                    "is $toFinalUnit ${if(toFinalUnit == 1.0) toUnitWeightEnum.units[1] else toUnitWeightEnum.units[2]}"
        }
        //one unit is a weight and the other is length
        else if((fromUnitLengthEnum != Length.NULL || toUnitLengthEnum != Length.NULL)
            && (fromUnitWeightEnum != Weight.NULL || toUnitWeightEnum != Weight.NULL)) {
            return "Conversion from " +
                    "${if(fromUnitLengthEnum != Length.NULL) fromUnitLengthEnum.units[2] else fromUnitWeightEnum.units[2]} " +
                    "to " +
                    "${if(toUnitLengthEnum != Length.NULL) toUnitLengthEnum.units[2] else toUnitWeightEnum.units[2]} " +
                    "is impossible"
        }
        //no unit is recognized
        else if((fromUnitLengthEnum == Length.NULL && toUnitLengthEnum == Length.NULL)
            && (fromUnitWeightEnum == Weight.NULL && toUnitWeightEnum == Weight.NULL)) {
            return "Conversion from ??? to ??? is impossible"
        }
        //only one unit is recognized
        else {
            if((fromUnitLengthEnum != Length.NULL || toUnitLengthEnum != Length.NULL)){
                return "Conversion from " +
                        "${if(fromUnitLengthEnum != Length.NULL) fromUnitLengthEnum.units[2] else "???"} " +
                        "to " +
                        "${if(toUnitLengthEnum != Length.NULL) toUnitLengthEnum.units[2] else "???"} " +
                        "is impossible"
            }
            else{
                return "Conversion from " +
                        "${if(fromUnitWeightEnum != Weight.NULL) fromUnitWeightEnum.units[2] else "???"} " +
                        "to " +
                        "${if(toUnitWeightEnum != Weight.NULL) toUnitWeightEnum.units[2] else "???"} " +
                        "is impossible"
            }
        }
    }
}

fun findLengthUnitEnum(fromUnit: String, toUnit:String) : Pair<Length, Length> {
    var fromUnitEnum: Length = Length.NULL
    var toUnitEnum: Length = Length.NULL

    for(length in Length.values()){
        for(unit in length.units){
            if (fromUnit.equals(unit, ignoreCase = true)){
                fromUnitEnum = length
            }
            if (toUnit.equals(unit, ignoreCase = true)){
                toUnitEnum = length
            }
        }
    }

    return Pair(fromUnitEnum, toUnitEnum)
}

fun findWeightUnitEnum(fromUnit: String, toUnit:String) : Pair<Weight, Weight> {
    var fromUnitEnum: Weight = Weight.NULL
    var toUnitEnum: Weight = Weight.NULL

    for(weight in Weight.values()){
        for(unit in weight.units){
            if (fromUnit.equals(unit, ignoreCase = true)){
                fromUnitEnum = weight
            }
            if (toUnit.equals(unit, ignoreCase = true)){
                toUnitEnum = weight
            }
        }
    }

    return Pair(fromUnitEnum, toUnitEnum)
}

fun findTempUnitEnum(fromUnit: String, toUnit:String) : Pair<Temperature, Temperature> {
    var fromUnitEnum: Temperature = Temperature.NULL
    var toUnitEnum: Temperature = Temperature.NULL

    for(temperature in Temperature.values()){
        for(unit in temperature.units){
            if (fromUnit.equals(unit, ignoreCase = true)){
                fromUnitEnum = temperature
            }
            if (toUnit.equals(unit, ignoreCase = true)){
                toUnitEnum = temperature
            }
        }
    }

    return Pair(fromUnitEnum, toUnitEnum)
}

fun K_TO_C(kelvin: Double): Double {
    return kelvin - 273.15
}

fun F_TO_C(fahrenheit: Double) : Double{
    return ((fahrenheit - 32.0) * (5.0/9.0))
}

fun C_TO_K(celsius: Double) : Double{
    return celsius + 273.15
}

fun C_TO_F(celsius: Double) : Double{
    return celsius * (9.0/5.0) + 32.0
}
