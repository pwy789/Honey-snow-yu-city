export type SkuCategory = {
  id:number
  name: string
  sort:number
  skus:sku[]
}
export type sku={
  id:number,
  name:string,
  dimensionId:number,
}
