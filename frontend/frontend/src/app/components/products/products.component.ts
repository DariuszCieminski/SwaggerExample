import { Component, ElementRef, ViewChild } from '@angular/core';
import { Product } from "../../models/product";
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ProductDetailsComponent } from './product-details/product-details.component';
import { AddProductComponent } from "./add-product/add-product.component";
import { ProductService } from "../../services/product.service";
import { HttpErrorResponse } from "@angular/common/http";
import { SnackbarService } from "../../services/snackbar.service";

@Component({
    selector: 'app-products',
    templateUrl: './products.component.html',
    styleUrls: ['./products.component.css']
})
export class ProductsComponent {
    allProducts: Product[] = [];
    paginated: Product[] = [];
    @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
    @ViewChild('search_box', {static: false}) searchBox: ElementRef;

    constructor(private productService: ProductService, private dialog: MatDialog, private snackBar: SnackbarService) {
    }

    onPageChange(event: PageEvent): void {
        let start: number = event.pageIndex * event.pageSize;
        let end: number = start + event.pageSize;
        this.paginated = this.allProducts.slice(start, end);
    }

    searchByProductName(): void {
        let value = this.searchBox.nativeElement.value;
        let productName = (value === '') ? {} : {nameContains: value};
        this.searchProducts(productName);
    }

    searchProducts(value): void {
        this.productService.getProducts(value)
            .subscribe(response => {
                this.allProducts = response;
                this.paginated = this.allProducts;
                this.paginator.length = this.paginated.length;
            }, error => this.handleError(error));
    }

    showProductDetails(p: Product): void {
        this.dialog.open(ProductDetailsComponent, {height: 'auto', width: '50%', data: {product: p}});
    }

    showAddProductDialog(): void {
        this.dialog.open(AddProductComponent, {height: 'auto', width: '30%', disableClose: true}).afterClosed()
            .subscribe(value => {
                if (typeof value === "object") {
                    return this.productService.addProduct(value)
                        .subscribe(product => this.showSnackBar("Product '" + product.name + "' was successfully added."),
                            error => this.handleError(error));
                }
            });
    }

    private showSnackBar(message: string): void {
        this.snackBar.showSnackbar(message, "OK", {duration: 0});
    }

    private handleError(error: HttpErrorResponse): void {
        this.showSnackBar(error.error + " with status " + error.status);
    }
}