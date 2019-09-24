module Main exposing (main)

import Browser
import Element exposing (column, el, px, row)
import Element.Background as Background
import Html exposing (div)
import Html.Attributes exposing (style)
import Theme


main =
    Browser.sandbox { init = 0, update = update, view = view }


type Msg
    = NoOp


update msg model =
    case msg of
        NoOp ->
            model


sidebar =
    column
        [ Background.color Theme.subtleLightColor
        , Element.width (300 |> px)
        , Element.height Element.fill
        ]
        []


canvas =
    el
        [ Element.width Element.fill
        , Element.height Element.fill
        ]
        (div
            [ style "width" "100%"
            , style "height" "100%"
            , style "background" (Theme.colorToString Theme.subtleDarkColor)
            ]
            []
            |> Element.html
        )


view model =
    Element.layout
        []
        (row
            [ Element.width Element.fill
            , Element.height Element.fill
            ]
            [ sidebar
            , canvas
            ]
        )
