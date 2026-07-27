package core

import "context"

type exactCoverResult struct {
	solutions    int
	triedRows    int
	selectedRows []int
}

type exactCoverNode struct {
	left, right *exactCoverNode
	up, down    *exactCoverNode
	column      *exactCoverColumn
	rowIndex    int
}

type exactCoverColumn struct {
	exactCoverNode
	size int
}

type exactCover struct {
	ctx           context.Context
	root          *exactCoverColumn
	columns       []*exactCoverColumn
	selectedRows  []int
	solutionRows  []int
	solutions     int
	triedRows     int
	solutionLimit int
}

func runExactCover(ctx context.Context, columnCount int, rows [][]int, solutionLimit int) exactCoverResult {
	if ctx == nil {
		ctx = context.Background()
	}
	cover := newExactCover(ctx, columnCount, rows, solutionLimit)
	cover.search(0)
	return exactCoverResult{
		solutions:    cover.solutions,
		triedRows:    cover.triedRows,
		selectedRows: cover.solutionRows,
	}
}

func newExactCover(ctx context.Context, columnCount int, rows [][]int, solutionLimit int) *exactCover {
	root := newExactCoverColumn()
	cover := &exactCover{
		ctx:           ctx,
		root:          root,
		columns:       make([]*exactCoverColumn, columnCount),
		selectedRows:  make([]int, columnCount),
		solutionLimit: solutionLimit,
	}

	previous := &root.exactCoverNode
	for i := range cover.columns {
		column := newExactCoverColumn()
		cover.columns[i] = column
		linkExactCoverNodes(previous, &column.exactCoverNode)
		previous = &column.exactCoverNode
	}
	linkExactCoverNodes(previous, &root.exactCoverNode)

	for rowIndex, columnIndexes := range rows {
		cover.addRow(rowIndex, columnIndexes)
	}
	return cover
}

func newExactCoverColumn() *exactCoverColumn {
	column := &exactCoverColumn{}
	node := &column.exactCoverNode
	node.left = node
	node.right = node
	node.up = node
	node.down = node
	node.column = column
	node.rowIndex = -1
	return column
}

func (c *exactCover) addRow(rowIndex int, columnIndexes []int) {
	var first, previous *exactCoverNode
	for _, columnIndex := range columnIndexes {
		column := c.columns[columnIndex]
		node := &exactCoverNode{
			column:   column,
			rowIndex: rowIndex,
			down:     &column.exactCoverNode,
			up:       column.up,
		}
		column.up.down = node
		column.up = node
		column.size++

		node.left = node
		node.right = node
		if first == nil {
			first = node
		} else {
			linkExactCoverNodes(previous, node)
		}
		previous = node
	}
	if first != nil {
		linkExactCoverNodes(previous, first)
	}
}

func (c *exactCover) search(depth int) bool {
	select {
	case <-c.ctx.Done():
		return true
	default:
	}

	root := &c.root.exactCoverNode
	if root.right == root {
		c.solutions++
		c.solutionRows = append([]int(nil), c.selectedRows[:depth]...)
		return c.solutions >= c.solutionLimit
	}

	column := c.smallestColumn()
	if column.size == 0 {
		return false
	}

	c.cover(column)
	for row := column.down; row != &column.exactCoverNode; row = row.down {
		c.triedRows++
		c.selectedRows[depth] = row.rowIndex

		for node := row.right; node != row; node = node.right {
			c.cover(node.column)
		}

		limitReached := c.search(depth + 1)

		for node := row.left; node != row; node = node.left {
			c.uncover(node.column)
		}
		if limitReached {
			c.uncover(column)
			return true
		}
	}
	c.uncover(column)
	return false
}

func (c *exactCover) smallestColumn() *exactCoverColumn {
	root := &c.root.exactCoverNode
	var smallest *exactCoverColumn
	for node := root.right; node != root; node = node.right {
		column := node.column
		if smallest == nil || column.size < smallest.size {
			smallest = column
		}
	}
	return smallest
}

func (c *exactCover) cover(column *exactCoverColumn) {
	column.right.left = column.left
	column.left.right = column.right
	for row := column.down; row != &column.exactCoverNode; row = row.down {
		for node := row.right; node != row; node = node.right {
			node.down.up = node.up
			node.up.down = node.down
			node.column.size--
		}
	}
}

func (c *exactCover) uncover(column *exactCoverColumn) {
	for row := column.up; row != &column.exactCoverNode; row = row.up {
		for node := row.left; node != row; node = node.left {
			node.column.size++
			node.down.up = node
			node.up.down = node
		}
	}
	column.right.left = &column.exactCoverNode
	column.left.right = &column.exactCoverNode
}

func linkExactCoverNodes(left, right *exactCoverNode) {
	left.right = right
	right.left = left
}
