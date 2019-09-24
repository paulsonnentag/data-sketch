module Theme exposing (colorToString, darkColor, highlightColor, subtleDarkColor, subtleLightColor)

import Element exposing (Color, rgb255, toRgb)


darkColor : Color
darkColor =
    rgb255 35 55 77


highlightColor : Color
highlightColor =
    rgb255 16 137 255


subtleDarkColor : Color
subtleDarkColor =
    rgb255 238 238 238


subtleLightColor : Color
subtleLightColor =
    rgb255 250 250 250


colorToString : Color -> String
colorToString color =
    let
        { red, green, blue, alpha } =
            toRgb color
    in
    "rgba("
        ++ String.fromInt (floor (red * 255))
        ++ ","
        ++ String.fromInt (floor (green * 255))
        ++ ","
        ++ String.fromInt (floor (blue * 255))
        ++ ","
        ++ String.fromFloat alpha
        ++ ")"
