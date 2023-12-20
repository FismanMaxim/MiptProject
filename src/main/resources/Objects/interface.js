function chooseReviewTab(className) {
	document.querySelector("#activeReviewBlock").id = "";
	document.querySelector("#activeReviewButton").id = "";
	
	document.querySelector("." + className + "Button").id = "activeReviewButton";
	document.querySelector("." + className + "Block").id = "activeReviewBlock";
}

let isFormOpened = 0;
function loginForm() {
	document.querySelector(".loginForm").style.display = "block";
	document.querySelector(".registerForm").style.display = "none";
	
	if (isFormOpened != 1) {
		isFormOpened = 1;
		requestAnimationFrame(openForm);
	} else {
		isFormOpened = 0;
		requestAnimationFrame(closeForm);
	}
}
function registerForm() {
	document.querySelector(".registerForm").style.display = "block";
	document.querySelector(".loginForm").style.display = "none";
		
	if (isFormOpened != 2) {
		isFormOpened = 2;
		requestAnimationFrame(openForm);
	} else {
		isFormOpened = 0;
		requestAnimationFrame(closeForm);
	}
}

function openForm() {
	document.querySelector(".registerBlock").style.display = "block";
	document.querySelector(".main").style.filter = "blur(4px)";
	document.querySelector(".rdc").style.display = "block";
	document.querySelector(".ldc").style.display = "block";
}
function closeForm() {
	isFormOpened = 0;
	document.querySelector(".registerBlock").style.display = "none";
	document.querySelector(".main").style.filter = "blur(0px)";
}

function editShares(id, name, cost) {
	if (activeId.id >= 0 && isActiveCompany.flag == false) {
		chosenCompany = new ChosenCompany(id, name);
		
		document.querySelector(".companyName").innerHTML = "Issuer: " + name;
		document.querySelector(".companyCost").innerHTML = "The share price is " + cost;
		document.querySelector(".transactionBlock").style.display = "flex";
		if (activeUser.shares[id] > 0) {
			document.querySelector(".transactionUserValue").innerHTML = "You have " + activeUser.shares[id] + " shares";
		} else {
			document.querySelector(".transactionUserValue").innerHTML = "You haven't got shares of this company";
		}
	}
}
function updateShares() {
	let id = chosenCompany.companyId;
	if (activeUser.shares[id] > 0) {
		document.querySelector(".transactionUserValue").innerHTML = "You have " + activeUser.shares[id] + " shares";
	} else {
		document.querySelector(".transactionUserValue").innerHTML = "You haven't got shares of this company";
	}
}
function removeTransactionForm() {
	document.querySelector(".transactionBlock").style.display = "none";
}
function removeTransactionsTabs() {
	let transactionsTabs = document.querySelectorAll(".transactionButton");
	transactionsTabs.forEach((tr) => {
		tr.style.display = "none";
	});
}
function confirmBuy() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".sharesTransactionConfirmBuyButton").style.display = "flex";
}
function confirmSel() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".sharesTransactionConfirmSelButton").style.display = "flex";
}
function updateUserBalance() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".budgetTransactionConfirmSelButton").style.display = "flex";
}
function updateCompanyBalance() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".budgetCompanyTransactionConfirmSelButton").style.display = "flex";
}
function updateUserName() {
	document.querySelector(".TCBText").style.display = "flex";
	document.querySelector(".changeUserNameTransactionButton").style.display = "flex";
}
function updateCompanyName() {
	document.querySelector(".TCBText").style.display = "flex";
	document.querySelector(".changeCompanyNameTransactionButton").style.display = "flex";
}
function updateThreshold() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".changeThresholdTransactionButton").style.display = "flex";
}
function updateSharesCost() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".changeSharesCostTransactionButton").style.display = "flex";
}
function updateSharesAmount() {
	document.querySelector(".TCB").style.display = "flex";
	document.querySelector(".changeSharesAmountTransactionButton").style.display = "flex";
}
function removeTransactionConfirmForm() {
	removeTransactionsTabs();
	document.querySelector(".lInputSharesDelta").value = "";
	document.querySelector(".rInputSharesDelta").value = "";
	document.querySelector(".TCB").style.display = "none";
	document.querySelector(".TCBText").style.display = "none";
}
function prepairPage() {
	getCompanies();
}

let sortedCompanyList;

function fillCompanyTable() {
	let table = document.querySelector(".sharesBlock");
	table.innerHTML = "<tr><th>Issuer</th><th>Share price</th><th>Available shares</th></tr>";
	
	try {
		sortedCompanyList.forEach((company) => {
			let companyBlock = document.createElement("tr");
			let html = [];
			html.push(
				"<td>",
				company.companyName,
				"</td><td>",
				company.sharePrice,
				"</td><td>"
			);
			if (activeId.id >= 0 && isActiveCompany.flag == false) {
				html.push(
					company.totalShares,
					"<div class=\"editButton\" onclick=\"editShares(",
					company.id,
					", \'",
					company.companyName,
					"\', ",
					company.sharePrice,
					")\"></div></td>"
				);
			} else {
				html.push(
					company.vacantShares
				);
			}
			companyBlock.innerHTML = html.join("");
			table.appendChild(companyBlock);
		});
	} catch(error) {
		console.error(error);
	}
}

function activateAccount() {
	document.querySelector(".rButtonContainer").style.display = "none";
	document.querySelector(".accountContainer").style.display = "flex";
	updateData();
}

function updateData() {
	if (isActiveCompany.flag) {
		updateCompanyData();
	} else {
		updateUserData();
	}
}
function updateUserData() {
	document.querySelector(".accountUserMenuContainer").style.display = "flex";
	document.querySelector(".userBalanceText").innerHTML = activeUser.money;
	document.querySelector(".nameText").innerHTML = activeUser.name;
}
function updateCompanyData() {
	document.querySelector(".accountCompanyMenuContainer").style.display = "flex";
	document.querySelector(".companyBalanceText").innerHTML = activeCompany.money;
	document.querySelector(".nameText").innerHTML = activeCompany.companyName;
	document.querySelector(".companySharesAmountText").innerHTML = activeCompany.totalShares;
	document.querySelector(".thresholdText").innerHTML = activeCompany.keyShareholderThreshold;
	document.querySelector(".companySharesCostText").innerHTML = activeCompany.sharePrice;
}
let isMenuOpened = false;
function openMenu(){
	isMenuOpened = !isMenuOpened;
	if (isActiveCompany.flag) {
		moveCompanyMenu();
	} else {
		moveMenu();
	}
}
function moveMenu() {
	if (isMenuOpened) {
		document.querySelector(".accountUserMenuContainer").style.right = "0px";
	} else {
		document.querySelector(".accountUserMenuContainer").style.right = "-300px";
	}
}
function moveCompanyMenu() {
	if (isMenuOpened) {
		document.querySelector(".accountCompanyMenuContainer").style.right = "0px";
	} else {
		document.querySelector(".accountCompanyMenuContainer").style.right = "-300px";
	}
}
function selectInput() {
	document.documentElement.style.setProperty('--input-color', '#FF0000');
	setTimeout(removeInputSelections, 1000);
}

function removeInputSelections() {
	document.documentElement.style.setProperty('--input-color', '#8e8e8e');
}
function closeDataContainer() {
	document.querySelector(".accountDataContainer").style.display = "none";
}
function getUserInfo() {
	document.querySelector(".accountDataContainer").style.display = "flex";
	let html = [];
	html.push(
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Name: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeUser.name,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Balance: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeUser.money,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Companies: </span></div>",
	);
	
	for (let companyId in activeUser.shares) {
		html.push(
			"<div class='personalDataRaw personalDataLevel2 flexable'><span>CompanyName: ",
			companyList.get(parseInt(companyId)).companyName,
			"</span><span>Shares amount: ",
			activeUser.shares[parseInt(companyId)],
			"</span></div>",
		)
	};
	
	document.querySelector(".personalDataBlock").innerHTML = html.join("");
}
async function getCompanyInfo() {
	document.querySelector(".accountDataContainer").style.display = "flex";
	let html = [];
	html.push(
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Name: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.companyName,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Balance: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.money,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Total shares: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.totalShares,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Available shares: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.vacantShares,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Shares price: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.sharePrice,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Threshold value: </span></div>",
		"<div class='personalDataRaw personalDataLevel2 flexable'><span>",
		activeCompany.keyShareholderThreshold,
		"</span></div>",
		
		"<div class='personalDataRaw personalDataLevel1 flexable'><span>Users: </span></div>",
	);
	
	
	for (let userId in activeCompany.users) {
		let user = await fetch(url + '/usr/' + userId + '', {
			method: 'GET', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
		})
			.then(response => response.json());
		
		user = user.user;
		
		html.push(
			"<div class='personalDataRaw personalDataLevel2 flexable'><span>UserID: ",
			user.id,
			"</span><span>Shares amount: ",
			user.shares[parseInt(activeCompany.id)],
			"</span><span>VIPAccount: ",
			isVip(user, activeCompany.id),
			"</span></div>",
		)
	};
	
	document.querySelector(".personalDataBlock").innerHTML = html.join("");
}

function isVip(user, companyId) {
	if (parseInt(user.shares[companyId]) >= parseInt(activeCompany.keyShareholderThreshold)) {
		return "&#10004;";
	} else {
		return "&#10008;";
	}
}

let isSortCommon = 0;
function setSortingFilter() {
	isSortCommon = (isSortCommon + 1) % 3;
	if (isSortCommon == 0) {
		document.querySelector(".sortFilterIcon").style.background = "url('Objects/Images/letter.png') no-repeat center center / cover";
	} else if (isSortCommon == 1) {
		document.querySelector(".sortFilterIcon").style.background = "url('Objects/Images/graphDown.png') no-repeat center center / cover";
	} else {
		document.querySelector(".sortFilterIcon").style.background = "url('Objects/Images/graphUp.png') no-repeat center center / cover";
	}
	getSortedCompanyList();
}
let isObtain = 1;
function setObtainingFilter() {
	if (activeId.id >= 0){
		isObtain = (isObtain + 1) % 3;
		if (isObtain == 1) {
			document.querySelector(".obtainFilterIcon").style.background = "url('Objects/Images/envelope.png') no-repeat center center / cover";
		} else if (isObtain == 2) {
			document.querySelector(".obtainFilterIcon").style.background = "url('Objects/Images/envelopeT.png') no-repeat center center / cover";
		} else {
			document.querySelector(".obtainFilterIcon").style.background = "url('Objects/Images/envelopeF.png') no-repeat center center / cover";
		}
		getSortedCompanyList();
	}
}
let filter = "";
document.querySelector(".tableFilterInput").addEventListener("input", () => {
	filter = document.querySelector(".tableFilterInput").value.toUpperCase();
	getSortedCompanyList();
});

function getSortedCompanyList() {
	sortedCompanyList = [];
	
	companyList.forEach((company, key) => {
		let fl1 = true;
		let fl2 = true;
		
		if ((filter != "" && !(company.companyName.includes(filter)))) {
			fl1 = false;
		}
		
		if (isObtain != 1 && activeId.id >= 0 && ((isObtain > 0) ^ (activeUser.shares[parseInt(company.id)] > 0))){
			fl2 = false;
		}
		
		if (fl1 && fl2) {
			sortedCompanyList.push(company);
		}
	});
	
	
	
	if (isSortCommon == 0) {
		sortedCompanyList = sortedCompanyList.sort((a, b) => a.companyName > b.companyName);
	} else if (isSortCommon == 1) {
		sortedCompanyList = sortedCompanyList.sort((a, b) => b.sharePrice > a.sharePrice);
	} else {
		sortedCompanyList = sortedCompanyList.sort((a, b) => a.sharePrice > b.sharePrice);
	}
	console.log(sortedCompanyList);
	fillCompanyTable();
}
