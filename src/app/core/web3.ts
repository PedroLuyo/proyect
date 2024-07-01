import { Injectable } from '@angular/core';
import Web3 from 'web3';
import { BehaviorSubject } from 'rxjs';

declare let window: any;

@Injectable({
  providedIn: 'root'
})
export class Web3Service {
  private web3: Web3 | undefined;
  private account: string | null = null;
  public accountsObservable = new BehaviorSubject<string | null>(null);

  constructor() {
    if (typeof window.ethereum !== 'undefined') {
      this.web3 = new Web3(window.ethereum);
      window.ethereum.enable().catch((error: any) => {
        console.error('Acceso denegado por el usuario', error);
      });
    } else if (typeof window.web3 !== 'undefined') {
      this.web3 = new Web3(window.web3.currentProvider);
    } else {
      console.warn('MetaMask no está instalado.');
    }
  }

  public async connectAccount(): Promise<Web3 | null> {
    if (!this.web3) {
      console.error('Proveedor Web3 no inicializado.');
      return null;
    }

    try {
      const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
      this.account = accounts[0];
      this.accountsObservable.next(this.account);
      return this.web3;
    } catch (error) {
      console.error('Acceso denegado por el usuario', error);
      return null;
    }
  }

  public getAccount(): string | null {
    return this.account;
  }

  public async refreshAccounts(): Promise<void> {
    if (this.web3) {
      const accounts = await this.web3.eth.getAccounts();
      this.account = accounts.length > 0 ? accounts[0] : null;
      this.accountsObservable.next(this.account);
    }
  }

  public getWeb3(): Web3 | undefined {
    return this.web3;
  }

  public getContract(abi: any, address: string): any {
    if (!this.web3) {
      throw new Error('Proveedor Web3 no inicializado.');
    }
    return new this.web3.eth.Contract(abi, address);
  }

  public async callContractMethod(contract: any, methodName: string, ...args: any[]): Promise<any> {
    if (!this.account) {
      throw new Error('Cuenta no conectada.');
    }
    return contract.methods[methodName](...args).call({ from: this.account });
  }

  public async sendContractMethod(contract: any, methodName: string, ...args: any[]): Promise<any> {
    if (!this.account) {
      throw new Error('Cuenta no conectada.');
    }
    return contract.methods[methodName](...args).send({ from: this.account });
  }
}
