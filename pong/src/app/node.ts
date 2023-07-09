export interface Node {
  type: 'FILE' | 'FOLDER',
  path: string,
  children: Node[]
}
