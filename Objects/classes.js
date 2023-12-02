const Company = class {
	constructor(id, companyName, totalShares, vacantShares, keyShareholderThreshold, money, sharePrice, users) {
		this.id = id;
		this.companyName = companyName;
		this.totalShares = totalShares;
		this.vacantShares = vacantShares;
		this.keyShareholderThreshold = keyShareholderThreshold;
		this.money = money;
		this.sharePrice = sharePrice;
		this.users = users;
	}
}

const User = class {
	constructor(id, userName, money, shares) {
		this.id = id;
		this.userName = userName;
		this.money = money;
		this.shares = shares;
	}
}

const ChosenCompany = class {
	constructor(companyName) {
		this.companyName = companyName;
	}
}
let chosenCompany = new ChosenCompany("");
