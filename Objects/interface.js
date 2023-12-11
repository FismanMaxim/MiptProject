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
	document.querySelector(".registerBlock").style.display = "none";
	document.querySelector(".main").style.filter = "blur(0px)";
}

function editShares(name, cost) {
	if (activeId.id >= 0 && isActiveCompany.flag == false) {
		chosenCompany = new ChosenCompany(name);
		
		document.querySelector(".companyName").innerHTML = "Shareholder: " + name;
		document.querySelector(".companyCost").innerHTML = "The share price is " + cost;
		document.querySelector(".transactionBlock").style.display = "flex";
		if (activeUser.shares[name] > 0) {
			document.querySelector(".transactionUserValue").innerHTML = "You have " + activeUser.shares[name] + " shares";
		} else {
			document.querySelector(".transactionUserValue").innerHTML = "You haven't got shares of this company";
		}
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

function fillCompanyTable() {
	let table = document.querySelector(".sharesBlock");
	table.innerHTML = "<tr><th>Shareholder</th><th>Share price</th><th>TotalShares</th></tr>";
	
	try {
		companyList.forEach((company, key) => {
			let companyBlock = document.createElement("tr");
			let html = [];
			html.push(
				"<td>",
				company.name,
				"</td><td>",
				company.sharePrice,
				"</td><td>"
			);
			if (activeId.id >= 0 && isActiveCompany.flag == false) {
				html.push(
					company.totalShares,
					"<div class=\"editButton\" onclick=\"editShares(\'",
					company.name,
					"\', ",
					company.sharePrice,
					")\"></div></td>"
				);
			} else {
				html.push(
					company.totalShares
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
	document.querySelector(".nameText").innerHTML = activeCompany.name;
	document.querySelector(".companySharesAmountText").innerHTML = activeCompany.totalShares;
	document.querySelector(".thresholdText").innerHTML = activeCompany.keySharesThreshold;
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
		"<div><span>Name: ",
		activeUser.name,
		"</span></div>",
		"<div><span>Balance: ",
		activeUser.money,
		"</span></div>"
	);
	document.querySelector(".personalDataBlock").innerHTML = html.join("");
	
	let tableHtml = ["<tr><td>Shareholders</td></tr>"];
	activeUser.shares.forEach((company, key) => {
		tableHtml.push(
			"<tr><td>",
			company.id,
			"</td></tr>"
		)
	});
	
	document.querySelector(".dataTable").innerHTML = tableHtml.join("");
}
function getCompanyInfo() {
	document.querySelector(".accountDataContainer").style.display = "flex";
	let html = [];
	html.push(
		"<div><span>Name: ",
		activeCompany.name,
		"</span></div>",
		"<div><span>Balance: ",
		activeCompany.money,
		"</span></div>",
		"<div><span>Total shares: ",
		activeCompany.totalShares,
		"</span></div>",
		"<div><span>Vacant shares: ",
		activeCompany.vacantShares,
		"</span></div>",
		"<div><span>Shares price: ",
		activeCompany.sharePrice,
		"</span></div>",
		"<div><span>Threshold value: ",
		activeCompany.keySharesThreshold,
		"</span></div>",
	);
	document.querySelector(".personalDataBlock").innerHTML = html.join("");
	
	let tableHtml = ["<tr><td>Shareholders</td></tr>"];
	activeCompany.users.forEach((user, key) => {
		tableHtml.push(
			"<tr><td>",
			user.id,
			"</td></tr>"
		)
	});
	
	document.querySelector(".dataTable").innerHTML = tableHtml.join("");
}
