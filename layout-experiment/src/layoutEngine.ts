const elementsById: { [key: string]: HTMLElement } = {};

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
        const { addedNodes, removedNodes, target } = mutationsList[i];

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

    console.log('mutations', mutationsList);
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