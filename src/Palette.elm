module Palette exposing
    ( blue500
    , colorToString
    , gray0
    , gray100
    , gray200
    , gray300
    , gray400
    , gray50
    , gray500
    , gray600
    , gray700
    , gray800
    , gray900
    , h1
    , h2
    , h3
    , label
    , largeSpacing
    , mediumSpacing
    , smallSpacing
    , xLargeSpacing
    , xSmallSpacing
    , xxSmallSpacing
    )

import Element exposing (Attribute, Color, rgb255, rgba255, toRgb)
import Element.Font as Font
import Element.Region as Region



-- COLORS
--noinspection ElmUnusedSymbol


gray0 : Color
gray0 =
    rgb255 255 255 255



--noinspection ElmUnusedSymbol


gray50 : Color
gray50 =
    rgb255 250 250 250



--noinspection ElmUnusedSymbol


gray100 : Color
gray100 =
    rgb255 245 245 245



--noinspection ElmUnusedSymbol


gray200 : Color
gray200 =
    rgb255 238 238 238



--noinspection ElmUnusedSymbol


gray300 : Color
gray300 =
    rgb255 224 224 224



--noinspection ElmUnusedSymbol


gray400 : Color
gray400 =
    rgb255 189 189 189



--noinspection ElmUnusedSymbol


gray500 : Color
gray500 =
    rgb255 158 158 158



--noinspection ElmUnusedSymbol


gray600 : Color
gray600 =
    rgb255 117 117 117



--noinspection ElmUnusedSymbol


gray700 : Color
gray700 =
    rgb255 97 97 97



--noinspection ElmUnusedSymbol


gray800 : Color
gray800 =
    rgb255 66 66 66



--noinspection ElmUnusedSymbol


gray900 : Color
gray900 =
    rgb255 33 33 33



--noinspection ElmUnusedSymbol


blue500 : Color
blue500 =
    rgba255 33 150 243 0.5


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



-- SPACING
--noinspection ElmUnusedSymbol


xxSmallSpacing : Int
xxSmallSpacing =
    4



--noinspection ElmUnusedSymbol


xSmallSpacing : Int
xSmallSpacing =
    8



--noinspection ElmUnusedSymbol


smallSpacing : Int
smallSpacing =
    16



--noinspection ElmUnusedSymbol


mediumSpacing : Int
mediumSpacing =
    32



--noinspection ElmUnusedSymbol


largeSpacing : Int
largeSpacing =
    64



--noinspection ElmUnusedSymbol


xLargeSpacing : Int
xLargeSpacing =
    128



-- FONTS
--noinspection ElmUnusedSymbol


fontScale : Int -> Int
fontScale n =
    round (12 * 1.2 ^ toFloat n)



--noinspection ElmUnusedSymbol


h1 : List (Attribute msg)
h1 =
    [ Font.size (fontScale 3)
    , Region.heading 1
    ]



--noinspection ElmUnusedSymbol


h2 : List (Attribute msg)
h2 =
    [ Font.size (fontScale 2)
    , Region.heading 2
    ]



--noinspection ElmUnusedSymbol


h3 : List (Attribute msg)
h3 =
    [ Font.size (fontScale 1)
    , Region.heading 3
    ]



--noinspection ElmUnusedSymbol


label =
    [ Font.size (fontScale 0)
    ]
