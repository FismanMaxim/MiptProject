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
	chosenCompany = new ChosenCompany(name);
	
	document.querySelector(".companyName").innerHTML = "Shareholder: " + name;
	document.querySelector(".companyCost").innerHTML = "The share price is " + cost;
	document.querySelector(".transactionBlock").style.display = "flex";
}
function removeTransitionForm() {
	document.querySelector(".transactionBlock").style.display = "none";
}

function confirmBuy() {
	document.querySelector(".transactionConfirmBlock").style.display = "flex";
	document.querySelector(".sharesTransactionConfirmBuyButton").style.display = "flex";
	document.querySelector(".sharesTransactionConfirmSelButton").style.display = "none";
}

function confirmSel() {
	document.querySelector(".transactionConfirmBlock").style.display = "flex";
	document.querySelector(".sharesTransactionConfirmBuyButton").style.display = "none";
	document.querySelector(".sharesTransactionConfirmSelButton").style.display = "flex";
}

function removeTransitionConfirmForm() {
	document.querySelector(".transactionConfirmBlock").style.display = "none";
}
function prepairPage() {
	getCompanies();
	
	fillCompanyTable();
}

function fillCompanyTable() {
	let table = document.querySelector(".sharesBlock");
	
	try {
		companyList.forEach((company, key) => {
			let companyBlock = document.createElement("tr");
			let html = []
			html.push(
				"<td>",
				company.companyName,
				"</td><td>",
				company.sharePrice,
				"</td><td>",
				company.keyShareholderThreshold,
				"<div class=\"editButton\" onclick=\"editShares(\'",
				company.companyName,
				"\', ",
				company.sharePrice,
				")\"></div></td>"
			);
			console.log(html.join(""));
			companyBlock.innerHTML = html.join("");
			table.appendChild(companyBlock);
		});
	} catch(error) {
		console.error(error);
	}
}
