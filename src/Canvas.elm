module Canvas exposing
    ( Box(..)
    , Path
    , Point
    , Rectangle
    , RectangleDefinition
    , addBox
    , boxFromRectangle
    , rectangleFromDefinition
    , updateBox
    )

import Array exposing (Array)
import Element exposing (Color)
import Palette


type Box
    = Box
        { rectangle : Rectangle
        , fill : Color
        , children : Array Box
        }


type alias Rectangle =
    { top : Int
    , left : Int
    , width : Int
    , height : Int
    }


type alias RectangleDefinition =
    { startPoint : Point, endPoint : Point }


type alias Point =
    { x : Int, y : Int }



-- PATH


type alias Path =
    List Int



-- CANVAS


addBox : Path -> Box -> Array Box -> Array Box
addBox path box boxes =
    case path of
        [] ->
            Array.push box boxes

        index :: _ ->
            case ( Array.get index boxes, List.tail path ) of
                ( Just childBox, Just tail ) ->
                    Array.set index (addBoxToBox tail box childBox) boxes

                _ ->
                    boxes


addBoxToBox : Path -> Box -> Box -> Box
addBoxToBox path box (Box parentBox) =
    case path of
        [] ->
            Box { parentBox | children = Array.push box parentBox.children }

        index :: _ ->
            case ( Array.get index parentBox.children, List.tail path ) of
                ( Just childBox, Just tail ) ->
                    Box { parentBox | children = Array.set index (addBoxToBox tail box childBox) parentBox.children }

                _ ->
                    Box parentBox


updateBox : Path -> (Box -> Box) -> Array Box -> Array Box
updateBox path update boxes =
    boxes



-- UTILS


rectangleFromDefinition : RectangleDefinition -> Rectangle
rectangleFromDefinition { startPoint, endPoint } =
    let
        left =
            min startPoint.x endPoint.x

        right =
            max startPoint.x endPoint.x

        top =
            min startPoint.y endPoint.y

        bottom =
            max startPoint.y endPoint.y
    in
    { left = left
    , top = top
    , width =
        right - left
    , height =
        bottom - top
    }


boxFromRectangle : Rectangle -> Box
boxFromRectangle rectangle =
    Box
        { rectangle = rectangle
        , fill = Palette.blue500
        , children = Array.empty
        }
