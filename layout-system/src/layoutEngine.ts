const nodesById: { [key: string]: LayoutNode } = {};

// types

interface Pixel {
    type: 'pixel'
    value: number
}

function pixel(value: number): Pixel {
    return {type: 'pixel', value};
}

interface Part {
    type: 'part',
    value: number
}

function part(value: number): Part {
    return {type: 'part', value}
}

interface Reference {
    type: 'reference'
    nodeId: string,
}

type Size = Pixel | Part | Reference | 'shrinkWrap'

type Position = Pixel | Reference

type LayoutDirection = 'up' | 'down' | 'left' | 'right'

interface LayoutNode {
    element: HTMLElement,
    dependencies: Array<LayoutNode>,
}

type Constraint<Value> = { value: Value, isDefault: boolean } | 'unconstrained'

function defaultConstraint(value: Value): Constraint<Value> {
    return {value, isEnabled: true, isDefault: true}
}

function constraint(value: Value): Constraint<Value> {
    return {value, isEnabled: true, isDefault: false}
}

interface BoxNode {
    id: string,
    width: Constraint<Size>,
    height: Constraint<Size>
    top: Constraint<Position>,
    right: Constraint<Position>,
    left: Constraint<Position>,
    bottom: Constraint<Position>,
    center: {
        x: Constraint<Position>,
        y: Constraint<Position>
    }
    padding: Constraint<Size>,
}

interface GroupNode extends BoxNode {
    spacing: Size
    layoutDirection: LayoutDirection
}

//@ts-ignore
window.elementsById = elementsById;

const config = {
    attributes: true,
    childList: true,
    subtree: true,
    attributeFilter: [
        // general
        'data-represents',

        // group
        'data-flow',
        'data-padding',
        'data-spacing',

        // size
        'data-width',
        'data-height',

        // position
        'data-top',
        'data-right',
        'data-bottom',
        'data-left'
    ]
};

// UTILS

function layoutNodeFromElement(element: HTMLElement): LayoutNode | null {
    const id = getIdOfElement(element);

    if (id == null) {
        return null
    }

    switch (element.tagName) {
        case 'LAYOUT-GROUP':
            const {height, width, top, right, bottom, left, center, padding, spacing, represents} = element.dataset;

            return {
                height: height !== undefined ? constraint(parseSizeExpression(height)) : defaultConstraint(pixel(50)),
                width: width !== undefined ? constraint(parseSizeExpression(width)) : defaultConstraint(pixel(50)),
                top: top !== undefined ? constraint(parsePositionExpression(top)) : 'unconstrained',
                right: right !== undefined ? constraint(parsePositionExpression((right)) : 'unconstrained',
                bottom: bottom !== undefined ? constraint(parsePositionExpression((bottom)) : 'unconstrained',
                left: left !== undefined ? constraint(parsePositionExpression((left)) : 'unconstrained',
                center: {
                    x: (center && center.x !== undefined) ? constraint(parsePositionExpression((left)) : 'unconstrained',
                    y: (center && center.y !== undefined) ? constraint(parsePositionExpression((left)) : 'unconstrained'
                }
                padding: padding !== undefined ? constraint(parseSizeExpression(padding)) : 'unconstrained',
                spacing: spacing !== undefined ? constraint(parseSizeExpression((spacing)) : 'unconstrained'
            };


        /*case 'LAYOUT-BOX': {
            const {height, width, top, right, bottom, left} = element.dataset;


            const boxNode: BoxNode = {
                height: height ? parseSizeConstraint(height) : defaultConstraint(pixel(50)),
                width: defaultConstraint(pixel(50)),
                top: 'unconstrained',
                right: 'unconstrained',
                bottom: 'unconstrained',
                left: 'unconstrained'
            }
        }*/
    }
}

function getIdOfElement(element: HTMLElement): String | null {
    const relativeId = element.dataset.represents;

    if (!relativeId) {
        return null;
    }

    const parent = element.parentElement;

    if (!parent || !parent.dataset.id) {
        return relativeId;
    }

    return `${parent.dataset.id}.${relativeId}`;
}

function parseSizeExpression(expression: string): Size | null {
    const pixelValue = parsePixelValue(expression);
    if (pixelValue !== null) {
        return pixelValue
    }

    return null;
}

function parsePositionExpression(expression: string): Position | null {
    const pixelValue = parsePixelValue(expression);
    if (pixelValue !== null) {
        return pixelValue
    }

    return null;
}


function parsePixelValue(expression): Pixel | null {
    const result = expression.match(/^(0|[1-9][0-9]*)px$/);

    if (result === null) {
        return result;
    }

    parseInt(result[1], 10);
}


function isLayoutElement(element: HTMLElement) {
    return (
        element.tagName === 'LAYOUT-CONTEXT' ||
        element.tagName === 'LAYOUT-GROUP' ||
        element.tagName === 'LAYOUT-BOX'
    )
}

function isLayoutContext(element: HTMLElement): boolean {
    return element.tagName === 'LAYOUT-CONTEXT';
}

function isLayoutContainer(element: HTMLElement): boolean {
    return element.tagName === 'LAYOUT-CONTEXT' || element.tagName === 'LAYOUT-GROUP';
}

function isLayoutChild(element: HTMLElement): boolean {
    return element.tagName === 'LAYOUT-GROUP' || element.tagName === 'LAYOUT-BOX';
}

function $msg(templateParts: TemplateStringsArray, ...expressions: Array<any>) {
    const formattedExpressions = expressions.map((expression: any) => {
        if (expression instanceof HTMLElement) {
            return elementToString(expression);
        }
        return expression
    })

    let result = '';

    for (let i = 0; i < templateParts.length; i++) {
        result += (i < formattedExpressions.length
            ? `${templateParts[i]}${formattedExpressions[i]}`
            : templateParts[i]);
    }

    return result
}

function elementToString(element: HTMLElement): String {
    if (element.dataset.id) {
        return `<${element.tagName} id=${element.dataset.id}>`
    }

    if (element.dataset.represents) {
        return `<${element.tagName} represents=${element.dataset.represents}>`
    }

    return `<${element.tagName}>`
}

function handleMutations(mutationsList: Array<MutationRecord>) {
    for (let i = 0; i < mutationsList.length; i++) {
        const {addedNodes, removedNodes, target} = mutationsList[i];

        if (!(target instanceof HTMLElement)) {
            continue;
        }

        for (let j = 0; j < addedNodes.length; j++) {
            const node = addedNodes[j];

            if (!(node instanceof HTMLElement)) {
                continue;
            }

            addElement(target, node);
        }

        for (let j = 0; j < removedNodes.length; j++) {
            const node = removedNodes[j];

            if (!(node instanceof HTMLElement)) {
                continue;
            }

            removeElement(removedNodes[j]);
        }
    }

    recalculateLayout();
};

function removeElement(node: Node) {
    console.log('remove node', node);
}

function addElement(parent: HTMLElement, element: HTMLElement) {

    if (isLayoutContext(element)) {
        if (isLayoutElement(parent)) {
            throw new Error($msg`${element} cannot be the child of a layout element but it has the parent ${parent})`)
        }

        addElementToIndex(element)
        return;
    }

    if (isLayoutChild(element)) {
        addElementToIndex(element, parent)
        return;
    }

    console.warn($msg`foreign element ${element} inside of ${parent}`);
}

function addElementToIndex(element: HTMLElement, parent: HTMLElement | null = null) {
    if (!element.dataset.represents) {
        throw new Error($msg`${element} is missing the representation attribute`)
    }

    const id: string = parent
        ? `${parent.dataset.id}.${element.dataset.represents}`
        : element.dataset.represents;

    element.dataset.id = id;
    elementsById[id] = element;

    const children = element.children;

    if (children.length === 0) {
        return;
    }

    if (!isLayoutContainer(element)) {
        throw new Error($msg`${element} is not a container but it has ${children.length} children`);
    }

    for (let i = 0; i < children.length; i++) {
        const child = children[i];

        if (!(child instanceof HTMLElement)) {
            continue;
        }

        addElement(element, child);
    }
}


function recalculateLayout() {
    const independentNodes = [];

    for (id in elementsById) {


    }

    for (let i = 0; i < .
    length;
    i++
)
    {

    }


}

const observer = new MutationObserver(handleMutations);

function mount(root: Element) {
    observer.observe(root, config);
}

function unmount() {
    observer.disconnect();
}

export default {
    mount,
    unmount
}