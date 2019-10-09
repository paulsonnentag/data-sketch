module Canvas exposing
    ( Box
    , Canvas
    , CanvasModel
    , Point
    , Rectangle
    , RectangleDefinition
    , addBox
    , boxFromRectangle
    , childrenOfBox
    , empty
    , rectangleFromDefinition
    , topLevelBoxes
    , updateBox
    )

import Dict exposing (Dict)
import Element exposing (Color)
import Palette


type Canvas
    = Canvas CanvasModel


type alias CanvasModel =
    { boxes : Dict Int Box
    , childrenByParent : Dict Int (Dict Int Box)
    , nextId : Int
    }


type alias Box =
    { rectangle : Rectangle
    , fill : Color
    , parentId : Maybe Int
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


empty : Canvas
empty =
    Canvas
        { boxes = Dict.empty
        , childrenByParent = Dict.empty
        , nextId = 0
        }



-- CANVAS


childrenOfBox : Canvas -> Int -> List ( Int, Box )
childrenOfBox (Canvas { childrenByParent }) id =
    Dict.get id childrenByParent
        |> Maybe.withDefault Dict.empty
        |> Dict.toList


topLevelBoxes : Canvas -> List ( Int, Box )
topLevelBoxes canvas =
    childrenOfBox canvas -1


addBox : Box -> Canvas -> Canvas
addBox box (Canvas canvas) =
    let
        { boxes, childrenByParent } =
            canvas

        boxesWithAddedBox =
            Dict.insert canvas.nextId box canvas.boxes
    in
    Canvas
        { canvas
            | boxes = boxesWithAddedBox
            , childrenByParent = getChildrenByParent boxesWithAddedBox
            , nextId = canvas.nextId + 1
        }


updateBox : Int -> (Box -> Box) -> Canvas -> Canvas
updateBox id update (Canvas canvas) =
    let
        boxesWithUpdatedBox =
            Dict.update
                id
                (\previousValue ->
                    case previousValue of
                        Nothing ->
                            Nothing

                        Just box ->
                            Just (update box)
                )
                canvas.boxes
    in
    Canvas
        { canvas
            | boxes = boxesWithUpdatedBox
            , childrenByParent = getChildrenByParent boxesWithUpdatedBox
        }


getChildrenByParent : Dict Int Box -> Dict Int (Dict Int Box)
getChildrenByParent boxes =
    Dict.foldl
        (\id box childrenByParent ->
            let
                parentId =
                    parentIdOfBox box
            in
            Dict.update
                parentId
                (\previousValue ->
                    let
                        children =
                            Maybe.withDefault Dict.empty previousValue
                    in
                    Just (Dict.insert id box children)
                )
                childrenByParent
        )
        Dict.empty
        boxes


parentIdOfBox : Box -> Int
parentIdOfBox box =
    Maybe.withDefault -1 box.parentId



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


boxFromRectangle : Maybe Int -> Rectangle -> Box
boxFromRectangle parentId rectangle =
    { rectangle = rectangle
    , fill = Palette.blue500
    , parentId = parentId
    }
