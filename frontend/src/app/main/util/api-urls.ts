export class ApiUrls {
    private static readonly BASE_URL: string = '{API_URL}';
    private static readonly USERS: string = '/api/users';
    private static readonly PRODUCTS: string = '/api/products';
    private static readonly ORDERS: string = '/api/orders';
    private static readonly UTIL: string = '/api/util';

    public static get login(): string {
        return this.BASE_URL + '/login';
    }

    public static get logout(): string {
        return this.BASE_URL + '/logout';
    }

    public static get swagger(): string {
        return this.BASE_URL + "/swagger-ui/index.html";
    }

    public static get ping(): string {
        return this.BASE_URL + this.UTIL + "/ping";
    }

    public static get users(): string {
        return this.BASE_URL + this.USERS;
    }

    public static get products(): string {
        return this.BASE_URL + this.PRODUCTS;
    }

    public static get orders(): string {
        return this.BASE_URL + this.ORDERS;
    }

    public static ordersForCurrentUser(): string {
        return this.BASE_URL + this.ORDERS + "/buyer";
    }

    public static ordersForBuyer(buyerId: number): string {
        return this.ordersForCurrentUser() + `/${buyerId}`;
    }
}